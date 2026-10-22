package br.com.fiap.shared.domain.valueObject;

import br.com.fiap.shared.domain.exception.InvalidEmailException;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("email must not be null or blank");
        }

        value = value.trim().toLowerCase();

        if (value.length() > 255) {
            throw new InvalidEmailException("email must have at most 255 characters");
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException("email is invalid");
        }
    }
}
