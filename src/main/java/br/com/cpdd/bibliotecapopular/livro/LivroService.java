package br.com.cpdd.bibliotecapopular.livro;

import br.com.cpdd.bibliotecapopular.exception.IsbnJaCadastradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LivroService {

    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Transactional
    public Livro criar(CriarLivroRequest request) {
        if (livroRepository.existsByIsbn(request.isbn())) {
            throw new IsbnJaCadastradoException();
        }

        Livro livro = new Livro(request.titulo(), request.autor(), request.isbn());
        return livroRepository.save(livro);
    }
}
