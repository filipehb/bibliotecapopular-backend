package br.com.cpdd.bibliotecapopular.exception;

public class ExemplarNaoEncontradoException extends RuntimeException {

    public ExemplarNaoEncontradoException() {
        super("Exemplar não encontrado");
    }
}
