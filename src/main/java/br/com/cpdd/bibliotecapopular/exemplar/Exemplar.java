package br.com.cpdd.bibliotecapopular.exemplar;

import br.com.cpdd.bibliotecapopular.livro.Livro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "exemplares")
public class Exemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusExemplar status = StatusExemplar.DISPONIVEL;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Exemplar() {
    }

    public Exemplar(Livro livro) {
        this.livro = livro;
    }

    public Long getId() {
        return id;
    }

    public Livro getLivro() {
        return livro;
    }

    public StatusExemplar getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void emprestar() {
        this.status = StatusExemplar.EMPRESTADO;
    }
}
