package br.com.fiap.shared.domain.valueObject;

import br.com.fiap.shared.domain.exception.DomainValidationException;

public record Author(String name) {

    public Author {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("Author name cannot be null or blank");
        }
    }
}
