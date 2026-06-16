package br.com.cpdd.bibliotecapopular.livro;

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
class LivroControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository livroRepository;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        livroRepository.deleteAll();
        adminToken = obterToken("admin", "admin123");
    }

    @Test
    void deveCriarLivroComSucesso() throws Exception {
        String body = """
                {
                  "titulo": "Dom Casmurro",
                  "autor": "Machado de Assis",
                  "isbn": "9788535911529"
                }
                """;

        mockMvc.perform(post("/livros")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor").value("Machado de Assis"))
                .andExpect(jsonPath("$.isbn").value("9788535911529"));

        assertThat(livroRepository.findAll()).hasSize(1);
    }

    @Test
    void deveRejeitarSemTitulo() throws Exception {
        String body = """
                {
                  "autor": "Machado de Assis",
                  "isbn": "9788535911529"
                }
                """;

        mockMvc.perform(post("/livros")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.titulo").value("Título é obrigatório"));
    }

    @Test
    void deveRejeitarSemAutor() throws Exception {
        String body = """
                {
                  "titulo": "Dom Casmurro",
                  "isbn": "9788535911529"
                }
                """;

        mockMvc.perform(post("/livros")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.autor").value("Autor é obrigatório"));
    }

    @Test
    void deveRejeitarSemIsbn() throws Exception {
        String body = """
                {
                  "titulo": "Dom Casmurro",
                  "autor": "Machado de Assis"
                }
                """;

        mockMvc.perform(post("/livros")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.isbn").value("ISBN é obrigatório"));
    }

    @Test
    void deveRejeitarIsbnComLetras() throws Exception {
        String body = """
                {
                  "titulo": "Dom Casmurro",
                  "autor": "Machado de Assis",
                  "isbn": "978853591152X"
                }
                """;

        mockMvc.perform(post("/livros")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.isbn").value("ISBN deve conter apenas números"));
    }

    @Test
    void deveRejeitarIsbnDuplicado() throws Exception {
        String body = """
                {
                  "titulo": "Dom Casmurro",
                  "autor": "Machado de Assis",
                  "isbn": "9788535911529"
                }
                """;

        mockMvc.perform(post("/livros")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        String bodyDuplicado = """
                {
                  "titulo": "Outro Livro",
                  "autor": "Outro Autor",
                  "isbn": "9788535911529"
                }
                """;

        mockMvc.perform(post("/livros")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyDuplicado))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("ISBN já cadastrado"));
    }

    @Test
    void deveRejeitarMembro() throws Exception {
        String membroToken = obterToken("membro", "membro123");
        String body = """
                {
                  "titulo": "Dom Casmurro",
                  "autor": "Machado de Assis",
                  "isbn": "9788535911529"
                }
                """;

        mockMvc.perform(post("/livros")
                        .header("Authorization", "Bearer " + membroToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarSemAutenticacao() throws Exception {
        String body = """
                {
                  "titulo": "Dom Casmurro",
                  "autor": "Machado de Assis",
                  "isbn": "9788535911529"
                }
                """;

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
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
