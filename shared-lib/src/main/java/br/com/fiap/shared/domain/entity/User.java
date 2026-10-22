package br.com.fiap.shared.domain.entity;

import br.com.fiap.shared.domain.valueObject.Email;
import br.com.fiap.shared.domain.valueObject.Name;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class User {

    private final UUID id;
    private final Name name;
    private final Email email;
    private final List<Book> books;
    private final boolean is_active;

    protected User(UUID id, Name name, Email email, List<Book> books, boolean is_active) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.books = books;
        this.is_active = is_active;
    }

    public UUID getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public boolean isActive() {
        return is_active;
    }

    public abstract String getType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
