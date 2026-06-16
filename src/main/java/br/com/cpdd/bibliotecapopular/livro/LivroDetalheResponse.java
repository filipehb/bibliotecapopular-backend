package br.com.cpdd.bibliotecapopular.livro;

import java.time.Instant;

public record LivroDetalheResponse(
        Long id,
        String titulo,
        String autor,
        String isbn,
        Instant createdAt,
        long exemplaresDisponiveis
) {

    public static LivroDetalheResponse from(Livro livro, long exemplaresDisponiveis) {
        return new LivroDetalheResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getCreatedAt(),
                exemplaresDisponiveis
        );
    }
}
