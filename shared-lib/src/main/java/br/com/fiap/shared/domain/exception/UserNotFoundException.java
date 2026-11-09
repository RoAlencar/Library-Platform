package br.com.fiap.shared.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(UUID id) {
        super("User not found: " + id);
    }
}
