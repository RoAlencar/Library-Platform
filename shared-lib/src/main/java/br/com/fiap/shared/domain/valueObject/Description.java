package br.com.fiap.shared.domain.valueObject;

import br.com.fiap.shared.domain.exception.DomainValidationException;

public record Description(String value) {

    public Description {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("Description cannot be null or blank");
        }
    }
}
