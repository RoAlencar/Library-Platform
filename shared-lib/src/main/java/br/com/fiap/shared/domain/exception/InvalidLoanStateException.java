package br.com.fiap.shared.domain.exception;

public class InvalidLoanStateException extends ConflictException {
    public InvalidLoanStateException(String message) {
        super(message);
    }
}
