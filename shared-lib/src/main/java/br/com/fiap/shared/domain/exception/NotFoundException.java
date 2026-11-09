package br.com.fiap.shared.domain.exception;

public abstract class NotFoundException extends DomainException {
    protected NotFoundException(String message) {
        super(message);
    }
}
