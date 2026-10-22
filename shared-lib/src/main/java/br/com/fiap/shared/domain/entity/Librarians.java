package br.com.fiap.shared.domain.entity;

import br.com.fiap.shared.domain.valueObject.Email;
import br.com.fiap.shared.domain.valueObject.Name;

import java.util.UUID;

public final class Librarians extends User {

    public Librarians(UUID id, Name name, Email email, boolean is_active) {
        super(id, name, email, is_active);
    }

    @Override
    public String getType() {
        return "LIBRARIAN";
    }

}
