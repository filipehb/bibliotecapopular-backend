package br.com.cpdd.bibliotecapopular.emprestimo;

import br.com.cpdd.bibliotecapopular.exception.ExemplarIndisponivelException;
import br.com.cpdd.bibliotecapopular.exception.ExemplarNaoEncontradoException;
import br.com.cpdd.bibliotecapopular.exemplar.Exemplar;
import br.com.cpdd.bibliotecapopular.exemplar.ExemplarRepository;
import br.com.cpdd.bibliotecapopular.exemplar.StatusExemplar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmprestimoService {

    private final ExemplarRepository exemplarRepository;
    private final EmprestimoRepository emprestimoRepository;

    public EmprestimoService(ExemplarRepository exemplarRepository, EmprestimoRepository emprestimoRepository) {
        this.exemplarRepository = exemplarRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @Transactional
    public Emprestimo solicitar(Long exemplarId, String usuario) {
        Exemplar exemplar = exemplarRepository.findByIdForUpdate(exemplarId)
                .orElseThrow(ExemplarNaoEncontradoException::new);

        if (exemplar.getStatus() != StatusExemplar.DISPONIVEL) {
            throw new ExemplarIndisponivelException();
        }

        exemplar.emprestar();
        Emprestimo emprestimo = new Emprestimo(exemplar, usuario);
        return emprestimoRepository.save(emprestimo);
    }
}
