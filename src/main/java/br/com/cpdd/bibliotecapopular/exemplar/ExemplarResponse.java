package br.com.cpdd.bibliotecapopular.exemplar;

import java.time.Instant;

public record ExemplarResponse(
        Long id,
        Long livroId,
        StatusExemplar status,
        Instant createdAt
) {

    public static ExemplarResponse from(Exemplar exemplar) {
        return new ExemplarResponse(
                exemplar.getId(),
                exemplar.getLivro().getId(),
                exemplar.getStatus(),
                exemplar.getCreatedAt()
        );
    }
}
