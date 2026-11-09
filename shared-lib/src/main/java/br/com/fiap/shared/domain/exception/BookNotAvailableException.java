package br.com.fiap.shared.domain.exception;

public class BookNotAvailableException extends ConflictException {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
