package br.com.cpdd.bibliotecapopular.livro;

import br.com.cpdd.bibliotecapopular.exception.IsbnJaCadastradoException;
import br.com.cpdd.bibliotecapopular.exception.LivroNaoEncontradoException;
import br.com.cpdd.bibliotecapopular.exemplar.ExemplarRepository;
import br.com.cpdd.bibliotecapopular.exemplar.StatusExemplar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final ExemplarRepository exemplarRepository;

    public LivroService(LivroRepository livroRepository, ExemplarRepository exemplarRepository) {
        this.livroRepository = livroRepository;
        this.exemplarRepository = exemplarRepository;
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
        boolean filtrarTitulo = StringUtils.hasText(titulo);
        boolean filtrarAutor = StringUtils.hasText(autor);
        String tituloPattern = filtrarTitulo ? "%" + titulo.toLowerCase() + "%" : "";
        String autorPattern = filtrarAutor ? "%" + autor.toLowerCase() + "%" : "";
        return livroRepository.buscar(filtrarTitulo, tituloPattern, filtrarAutor, autorPattern);
    }

    @Transactional(readOnly = true)
    public LivroDetalheResponse buscarDetalhesPorId(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(LivroNaoEncontradoException::new);
        long disponiveis = exemplarRepository.countByLivro_IdAndStatus(id, StatusExemplar.DISPONIVEL);
        return LivroDetalheResponse.from(livro, disponiveis);
    }
}
