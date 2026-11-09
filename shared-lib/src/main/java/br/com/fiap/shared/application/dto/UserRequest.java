package br.com.fiap.shared.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequest(

        @NotBlank
        @Size(min = 3, max = 255)
        String name,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        /* LIBRARY_MEMBER (default) or LIBRARIAN */
        @Pattern(regexp = "(?i)LIBRARY_MEMBER|LIBRARIAN", message = "must be LIBRARY_MEMBER or LIBRARIAN")
        String type,

        /* Defaults to true */
        Boolean active) {
}
