package br.com.fiap.shared.domain.valueObject;

import br.com.fiap.shared.domain.exception.DomainValidationException;

public record ISBN(String value) {

    public ISBN {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("ISBN cannot be null or blank");
        }

        String normalized = value.replace("-", "").replace(" ", "");

        if (!normalized.matches("\\d{13}|\\d{9}[\\dXx]")) {
            throw new DomainValidationException("ISBN must contain 10 or 13 valid-format characters");
        }

        value = normalized;
    }
}