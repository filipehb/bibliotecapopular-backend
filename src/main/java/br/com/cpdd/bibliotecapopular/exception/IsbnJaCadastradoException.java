package br.com.cpdd.bibliotecapopular.exception;

public class IsbnJaCadastradoException extends RuntimeException {

    public IsbnJaCadastradoException() {
        super("ISBN já cadastrado");
    }
}
