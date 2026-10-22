package br.com.fiap.shared.domain.valueObject;

public record Author(String name) {

    public Author {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Author name cannot be null or blank");
        }
    }
}
