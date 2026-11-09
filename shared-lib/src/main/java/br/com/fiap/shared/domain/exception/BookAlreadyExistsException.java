package br.com.fiap.shared.domain.exception;

public class BookAlreadyExistsException extends ConflictException {
    public BookAlreadyExistsException(String message) {
        super(message);
    }
}
