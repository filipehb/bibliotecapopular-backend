package br.com.cpdd.bibliotecapopular.exemplar;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/livros/{livroId}/exemplares")
public class ExemplarController {

    private final ExemplarService exemplarService;

    public ExemplarController(ExemplarService exemplarService) {
        this.exemplarService = exemplarService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExemplarResponse> cadastrar(@PathVariable Long livroId) {
        Exemplar exemplar = exemplarService.cadastrar(livroId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ExemplarResponse.from(exemplar));
    }
}
