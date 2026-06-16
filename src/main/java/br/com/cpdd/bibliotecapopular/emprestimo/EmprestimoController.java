package br.com.cpdd.bibliotecapopular.emprestimo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exemplares/{exemplarId}/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBRO')")
    public ResponseEntity<EmprestimoResponse> solicitar(@PathVariable Long exemplarId) {
        String usuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Emprestimo emprestimo = emprestimoService.solicitar(exemplarId, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(EmprestimoResponse.from(emprestimo));
    }
}
