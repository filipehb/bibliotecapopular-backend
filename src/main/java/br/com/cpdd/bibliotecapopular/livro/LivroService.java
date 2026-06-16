package br.com.cpdd.bibliotecapopular.livro;

import br.com.cpdd.bibliotecapopular.exception.IsbnJaCadastradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

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

    @Transactional(readOnly = true)
    public List<Livro> buscar(String titulo, String autor) {
        String tituloFiltro = StringUtils.hasText(titulo) ? titulo : null;
        String autorFiltro = StringUtils.hasText(autor) ? autor : null;
        return livroRepository.buscar(tituloFiltro, autorFiltro);
    }
}
