package br.com.cpdd.bibliotecapopular.exemplar;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {

    long countByLivro_IdAndStatus(Long livroId, StatusExemplar status);
}
