package br.com.fiap.shared.domain.valueObject;

public record BookName(String value) {

    public BookName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Book name cannot be null or blank");
        }

        if (value.length() > 255) {
            throw new IllegalArgumentException("Book name cannot exceed 255 characters");
        }
    }
}
