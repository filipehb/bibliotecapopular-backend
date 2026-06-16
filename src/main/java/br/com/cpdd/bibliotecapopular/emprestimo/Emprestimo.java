package br.com.cpdd.bibliotecapopular.emprestimo;

import br.com.cpdd.bibliotecapopular.exemplar.Exemplar;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "emprestimos")
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exemplar_id", nullable = false)
    private Exemplar exemplar;

    @Column(nullable = false)
    private String usuario;

    @Column(nullable = false, updatable = false)
    private Instant dataEmprestimo = Instant.now();

    protected Emprestimo() {
    }

    public Emprestimo(Exemplar exemplar, String usuario) {
        this.exemplar = exemplar;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public Exemplar getExemplar() {
        return exemplar;
    }

    public String getUsuario() {
        return usuario;
    }

    public Instant getDataEmprestimo() {
        return dataEmprestimo;
    }
}
