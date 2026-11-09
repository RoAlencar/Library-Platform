package br.com.fiap.shared.domain.exception;

import java.util.UUID;

public class InactiveUserException extends DomainException {
    public InactiveUserException(UUID userId) {
        super("User is inactive and cannot borrow or reserve books: " + userId);
    }
}
