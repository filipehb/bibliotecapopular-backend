package br.com.cpdd.bibliotecapopular.livro;

import java.time.Instant;

public record LivroResponse(
        Long id,
        String titulo,
        String autor,
        String isbn,
        Instant createdAt
) {

    public static LivroResponse from(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getCreatedAt()
        );
    }
}
