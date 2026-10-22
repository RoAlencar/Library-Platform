package br.com.fiap.shared.domain.valueObject;

public record Description(String value) {

    public Description {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
    }
}
