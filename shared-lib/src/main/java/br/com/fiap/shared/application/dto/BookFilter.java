package br.com.fiap.shared.application.dto;

public record BookFilter(String name, String author, String isbn) {

    public BookFilter {
        name = blankToNull(name);
        author = blankToNull(author);
        isbn = isbn == null ? null : blankToNull(isbn.replace("-", "").replace(" ", "").toUpperCase());
    }

    public static BookFilter empty() {
        return new BookFilter(null, null, null);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
