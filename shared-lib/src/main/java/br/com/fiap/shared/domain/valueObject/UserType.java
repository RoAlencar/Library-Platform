package br.com.fiap.shared.domain.valueObject;

import br.com.fiap.shared.domain.entity.Librarians;
import br.com.fiap.shared.domain.entity.LibraryMember;
import br.com.fiap.shared.domain.entity.User;
import br.com.fiap.shared.domain.exception.DomainValidationException;

import java.util.Arrays;
import java.util.UUID;

public enum UserType {
    LIBRARY_MEMBER,
    LIBRARIAN;

    public static UserType from(String value) {
        if (value == null || value.isBlank()) {
            return LIBRARY_MEMBER;
        }
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new DomainValidationException("Invalid user type: " + value));
    }

    public User create(UUID id, Name name, Email email, boolean active) {
        return switch (this) {
            case LIBRARY_MEMBER -> new LibraryMember(id, name, email, active);
            case LIBRARIAN -> new Librarians(id, name, email, active);
        };
    }
}
