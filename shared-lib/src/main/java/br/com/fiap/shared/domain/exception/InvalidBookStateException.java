package br.com.fiap.shared.domain.exception;

public class InvalidBookStateException extends ConflictException {
    public InvalidBookStateException(String message) {
        super(message);
    }
}
