package br.com.fiap.shared.domain.valueObject;

import br.com.fiap.shared.domain.exception.DomainValidationException;

public record BookName(String value) {

    public BookName {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("Book name cannot be null or blank");
        }

        if (value.length() > 255) {
            throw new DomainValidationException("Book name cannot exceed 255 characters");
        }
    }
}
