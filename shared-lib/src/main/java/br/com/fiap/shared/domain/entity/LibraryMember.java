package br.com.fiap.shared.domain.entity;

import br.com.fiap.shared.domain.valueObject.Email;
import br.com.fiap.shared.domain.valueObject.Name;

import java.util.List;
import java.util.UUID;

public final class LibraryMember extends User {

    public LibraryMember(UUID id, Name name, Email email, List<Book> books, boolean is_active) {
        super(id, name, email, books, is_active);
    }

    public LibraryMember(UUID id, Name name, Email email, boolean is_active) {
        this(id, name, email, List.of(), is_active);
    }

    @Override
    public String getType() {
        return "LIBRARY_MEMBER";
    }

}
