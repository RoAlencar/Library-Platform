package br.com.fiap.shared.domain.exception;

import java.util.UUID;

public class BookNotFoundException extends NotFoundException {
    public BookNotFoundException(UUID id) {
        super("Book not found: " + id);
    }
}
