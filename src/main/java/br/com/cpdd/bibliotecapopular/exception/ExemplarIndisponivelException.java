package br.com.cpdd.bibliotecapopular.exception;

public class ExemplarIndisponivelException extends RuntimeException {

    public ExemplarIndisponivelException() {
        super("Exemplar indisponível para empréstimo");
    }
}
