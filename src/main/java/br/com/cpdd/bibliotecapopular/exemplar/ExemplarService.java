package br.com.cpdd.bibliotecapopular.exemplar;

import br.com.cpdd.bibliotecapopular.exception.LivroNaoEncontradoException;
import br.com.cpdd.bibliotecapopular.livro.Livro;
import br.com.cpdd.bibliotecapopular.livro.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExemplarService {

    private final ExemplarRepository exemplarRepository;
    private final LivroRepository livroRepository;

    public ExemplarService(ExemplarRepository exemplarRepository, LivroRepository livroRepository) {
        this.exemplarRepository = exemplarRepository;
        this.livroRepository = livroRepository;
    }

    @Transactional
    public Exemplar cadastrar(Long livroId) {
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(LivroNaoEncontradoException::new);

        Exemplar exemplar = new Exemplar(livro);
        return exemplarRepository.save(exemplar);
    }
}
