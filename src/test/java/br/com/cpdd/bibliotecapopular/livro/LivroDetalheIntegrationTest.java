package br.com.cpdd.bibliotecapopular.livro;

import br.com.cpdd.bibliotecapopular.exemplar.Exemplar;
import br.com.cpdd.bibliotecapopular.exemplar.ExemplarRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LivroDetalheIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private ExemplarRepository exemplarRepository;

    private String membroToken;
    private Livro livro;

    @BeforeEach
    void setUp() throws Exception {
        exemplarRepository.deleteAll();
        livroRepository.deleteAll();

        livro = livroRepository.save(new Livro("Dom Casmurro", "Machado de Assis", "9788535911529"));
        exemplarRepository.save(new Exemplar(livro));
        exemplarRepository.save(new Exemplar(livro));

        membroToken = obterToken("membro", "membro123");
    }

    @Test
    void deveRetornarDetalhesEDisponibilidade() throws Exception {
        mockMvc.perform(get("/livros/{id}", livro.getId())
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(livro.getId()))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor").value("Machado de Assis"))
                .andExpect(jsonPath("$.isbn").value("9788535911529"))
                .andExpect(jsonPath("$.exemplaresDisponiveis").value(2));
    }

    @Test
    void deveRetornarZeroQuandoSemExemplares() throws Exception {
        exemplarRepository.deleteAll();

        mockMvc.perform(get("/livros/{id}", livro.getId())
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exemplaresDisponiveis").value(0));
    }

    @Test
    void deveRetornar404ParaLivroInexistente() throws Exception {
        mockMvc.perform(get("/livros/{id}", 999)
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Livro não encontrado"));
    }

    @Test
    void devePermitirMembroConsultar() throws Exception {
        mockMvc.perform(get("/livros/{id}", livro.getId())
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isOk());
    }

    @Test
    void deveRejeitarSemAutenticacao() throws Exception {
        mockMvc.perform(get("/livros/{id}", livro.getId()))
                .andExpect(status().isUnauthorized());
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
