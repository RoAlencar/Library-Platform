package br.com.fiap.shared.domain.exception;

public class ResourceInUseException extends ConflictException {
    public ResourceInUseException(String message) {
        super(message);
    }
}
