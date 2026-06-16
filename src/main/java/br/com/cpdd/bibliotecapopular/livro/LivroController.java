package br.com.cpdd.bibliotecapopular.livro;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRO')")
    public List<LivroResponse> buscar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor) {
        return livroService.buscar(titulo, autor).stream()
                .map(LivroResponse::from)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LivroResponse> criar(@Valid @RequestBody CriarLivroRequest request) {
        Livro livro = livroService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(LivroResponse.from(livro));
    }
}
