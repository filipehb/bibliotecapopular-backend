package br.com.cpdd.bibliotecapopular.exemplar;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExemplarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private ExemplarRepository exemplarRepository;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        exemplarRepository.deleteAll();
        livroRepository.deleteAll();
        adminToken = obterToken("admin", "admin123");
    }

    @Test
    void deveCadastrarExemplarComSucesso() throws Exception {
        Livro livro = livroRepository.save(new Livro("Dom Casmurro", "Machado de Assis", "9788535911529"));

        mockMvc.perform(post("/livros/{livroId}/exemplares", livro.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.livroId").value(livro.getId()))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"));

        assertThat(exemplarRepository.findAll()).hasSize(1);
    }

    @Test
    void deveRejeitarLivroInexistente() throws Exception {
        mockMvc.perform(post("/livros/{livroId}/exemplares", 999)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Livro não encontrado"));

        assertThat(exemplarRepository.findAll()).isEmpty();
    }

    @Test
    void deveRejeitarMembro() throws Exception {
        Livro livro = livroRepository.save(new Livro("Dom Casmurro", "Machado de Assis", "9788535911529"));
        String membroToken = obterToken("membro", "membro123");

        mockMvc.perform(post("/livros/{livroId}/exemplares", livro.getId())
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isForbidden());

        assertThat(exemplarRepository.findAll()).isEmpty();
    }

    @Test
    void deveRejeitarSemAutenticacao() throws Exception {
        Livro livro = livroRepository.save(new Livro("Dom Casmurro", "Machado de Assis", "9788535911529"));

        mockMvc.perform(post("/livros/{livroId}/exemplares", livro.getId()))
                .andExpect(status().isUnauthorized());

        assertThat(exemplarRepository.findAll()).isEmpty();
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
