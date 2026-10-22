package br.com.fiap.shared.domain.valueObject;

public record ISBN(String value) {

    public ISBN {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or blank");
        }

        String normalized = value.replace("-", "").replace(" ", "");

        if (!normalized.matches("\\d{13}|\\d{9}[\\dXx]")) {
            throw new IllegalArgumentException("ISBN must contain 10 or 13 valid-format characters");
        }

        value = normalized;
    }
}