package br.com.cpdd.bibliotecapopular.livro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CriarLivroRequest(
        @NotBlank(message = "Título é obrigatório")
        String titulo,

        @NotBlank(message = "Autor é obrigatório")
        String autor,

        @NotBlank(message = "ISBN é obrigatório")
        @Pattern(regexp = "^[0-9]+$", message = "ISBN deve conter apenas números")
        String isbn
) {
}
