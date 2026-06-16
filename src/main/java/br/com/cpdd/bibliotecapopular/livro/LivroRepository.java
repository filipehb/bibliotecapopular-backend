package br.com.cpdd.bibliotecapopular.livro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    boolean existsByIsbn(String isbn);

    @Query("""
            SELECT l FROM Livro l
            WHERE (:filtrarTitulo = false OR LOWER(l.titulo) LIKE :tituloPattern)
              AND (:filtrarAutor = false OR LOWER(l.autor) LIKE :autorPattern)
            """)
    List<Livro> buscar(
            @Param("filtrarTitulo") boolean filtrarTitulo,
            @Param("tituloPattern") String tituloPattern,
            @Param("filtrarAutor") boolean filtrarAutor,
            @Param("autorPattern") String autorPattern);
}
