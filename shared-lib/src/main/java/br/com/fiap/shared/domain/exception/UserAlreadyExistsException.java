package br.com.fiap.shared.domain.exception;

public class UserAlreadyExistsException extends DomainValidationException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
