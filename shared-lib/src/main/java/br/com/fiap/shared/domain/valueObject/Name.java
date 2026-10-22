package br.com.fiap.shared.domain.valueObject;

import br.com.fiap.shared.domain.exception.InvalidNameException;

public record Name(String value) {

    public Name {
        if (value == null || value.isBlank()) {
            throw new InvalidNameException("name must not be null or blank");
        }

        value = value.trim();

        if (value.length() < 3) {
            throw new InvalidNameException("name must have at least 3 characters");
        }

        if (value.length() > 255) {
            throw new InvalidNameException("name must have at most 255 characters");
        }
    }
}