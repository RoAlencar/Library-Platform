package br.com.fiap.shared.domain.exception;

public class ValidationException extends DomainException{
    public ValidationException(String message) {
        super(message);
    }
}
