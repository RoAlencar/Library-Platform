package br.com.fiap.shared.domain.exception;

public abstract class ConflictException extends DomainException {
    protected ConflictException(String message) {
        super(message);
    }
}
