package br.com.fiap.shared.domain.exception;

public class InvalidNameException extends DomainValidationException {

    public InvalidNameException(String message) {
        super(message);
    }
}
