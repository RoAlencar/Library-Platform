package br.com.fiap.shared.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BookRequest(

        @NotBlank
        @Size(max = 255)
        String name,

        @NotBlank
        @Size(max = 255)
        String author,

        @Size(max = 2000)
        String description,

        @NotBlank
        @Pattern(regexp = "[\\d\\- ]{9,17}[\\dXx]", message = "must be a valid ISBN-10 or ISBN-13")
        String isbn) {
}
