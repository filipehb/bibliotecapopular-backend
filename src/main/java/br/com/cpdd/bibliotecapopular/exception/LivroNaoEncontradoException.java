package br.com.cpdd.bibliotecapopular.exception;

public class LivroNaoEncontradoException extends RuntimeException {

    public LivroNaoEncontradoException() {
        super("Livro não encontrado");
    }
}
