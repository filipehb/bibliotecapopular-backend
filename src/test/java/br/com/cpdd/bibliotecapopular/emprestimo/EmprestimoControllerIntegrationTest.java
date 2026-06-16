package br.com.cpdd.bibliotecapopular.emprestimo;

import br.com.cpdd.bibliotecapopular.exemplar.Exemplar;
import br.com.cpdd.bibliotecapopular.exemplar.ExemplarRepository;
import br.com.cpdd.bibliotecapopular.exemplar.StatusExemplar;
import br.com.cpdd.bibliotecapopular.livro.Livro;
import br.com.cpdd.bibliotecapopular.livro.LivroRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmprestimoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private ExemplarRepository exemplarRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    private String membroToken;
    private String adminToken;
    private Livro livro;
    private Exemplar exemplar;

    @BeforeEach
    void setUp() throws Exception {
        emprestimoRepository.deleteAll();
        exemplarRepository.deleteAll();
        livroRepository.deleteAll();

        livro = livroRepository.save(new Livro("Dom Casmurro", "Machado de Assis", "9788535911529"));
        exemplar = exemplarRepository.save(new Exemplar(livro));
        exemplarRepository.save(new Exemplar(livro));

        membroToken = obterToken("membro", "membro123");
        adminToken = obterToken("admin", "admin123");
    }

    @Test
    void deveSolicitarEmprestimoComSucesso() throws Exception {
        mockMvc.perform(post("/exemplares/{exemplarId}/emprestimos", exemplar.getId())
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.exemplarId").value(exemplar.getId()))
                .andExpect(jsonPath("$.livroId").value(livro.getId()))
                .andExpect(jsonPath("$.usuario").value("membro"));

        assertThat(emprestimoRepository.findAll()).hasSize(1);
        assertThat(exemplarRepository.findById(exemplar.getId()).orElseThrow().getStatus())
                .isEqualTo(StatusExemplar.EMPRESTADO);
    }

    @Test
    void devePermitirAdminSolicitar() throws Exception {
        mockMvc.perform(post("/exemplares/{exemplarId}/emprestimos", exemplar.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario").value("admin"));
    }

    @Test
    void deveRejeitarExemplarJaEmprestado() throws Exception {
        mockMvc.perform(post("/exemplares/{exemplarId}/emprestimos", exemplar.getId())
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/exemplares/{exemplarId}/emprestimos", exemplar.getId())
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Exemplar indisponível para empréstimo"));
    }

    @Test
    void deveRejeitarExemplarInexistente() throws Exception {
        mockMvc.perform(post("/exemplares/{exemplarId}/emprestimos", 999)
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Exemplar não encontrado"));
    }

    @Test
    void deveRejeitarSemAutenticacao() throws Exception {
        mockMvc.perform(post("/exemplares/{exemplarId}/emprestimos", exemplar.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveReduzirDisponibilidadeAposEmprestimo() throws Exception {
        mockMvc.perform(get("/livros/{id}", livro.getId())
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exemplaresDisponiveis").value(2));

        mockMvc.perform(post("/exemplares/{exemplarId}/emprestimos", exemplar.getId())
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/livros/{id}", livro.getId())
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exemplaresDisponiveis").value(1));
    }

    private String obterToken(String username, String password) throws Exception {
        String body = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }
}
