package br.com.cpdd.bibliotecapopular.exemplar;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {

    long countByLivro_IdAndStatus(Long livroId, StatusExemplar status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Exemplar e WHERE e.id = :id")
    Optional<Exemplar> findByIdForUpdate(@Param("id") Long id);
}
