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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LivroBuscaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository livroRepository;

    private String membroToken;

    @BeforeEach
    void setUp() throws Exception {
        livroRepository.deleteAll();
        livroRepository.save(new Livro("Dom Casmurro", "Machado de Assis", "9788535911529"));
        livroRepository.save(new Livro("Memórias Póstumas", "Machado de Assis", "9788535909557"));
        livroRepository.save(new Livro("O Cortiço", "Aluísio Azevedo", "9788501050000"));
        membroToken = obterToken("membro", "membro123");
    }

    @Test
    void deveBuscarPorTituloCaseInsensitive() throws Exception {
        mockMvc.perform(get("/livros")
                        .param("titulo", "CASMURRO")
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"));
    }

    @Test
    void deveBuscarPorAutorCaseInsensitive() throws Exception {
        mockMvc.perform(get("/livros")
                        .param("autor", "ASSIS")
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].autor").value("Machado de Assis"))
                .andExpect(jsonPath("$[1].autor").value("Machado de Assis"));
    }

    @Test
    void deveBuscarPorParteDoTexto() throws Exception {
        mockMvc.perform(get("/livros")
                        .param("autor", "de ass")
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$[1].titulo").value("Memórias Póstumas"));
    }

    @Test
    void deveRetornarListaVaziaQuandoSemResultado() throws Exception {
        mockMvc.perform(get("/livros")
                        .param("titulo", "inexistente")
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deveListarTodosSemFiltros() throws Exception {
        mockMvc.perform(get("/livros")
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void devePermitirMembroBuscar() throws Exception {
        mockMvc.perform(get("/livros")
                        .param("autor", "Azevedo")
                        .header("Authorization", "Bearer " + membroToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].titulo").value("O Cortiço"));
    }

    @Test
    void deveRejeitarSemAutenticacao() throws Exception {
        mockMvc.perform(get("/livros"))
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
