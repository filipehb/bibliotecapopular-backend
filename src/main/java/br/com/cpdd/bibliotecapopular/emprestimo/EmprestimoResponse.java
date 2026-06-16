package br.com.cpdd.bibliotecapopular.emprestimo;

import java.time.Instant;

public record EmprestimoResponse(
        Long id,
        Long exemplarId,
        Long livroId,
        String usuario,
        Instant dataEmprestimo
) {

    public static EmprestimoResponse from(Emprestimo emprestimo) {
        return new EmprestimoResponse(
                emprestimo.getId(),
                emprestimo.getExemplar().getId(),
                emprestimo.getExemplar().getLivro().getId(),
                emprestimo.getUsuario(),
                emprestimo.getDataEmprestimo()
        );
    }
}
