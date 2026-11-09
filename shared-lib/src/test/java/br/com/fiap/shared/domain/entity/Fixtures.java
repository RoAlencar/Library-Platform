package br.com.fiap.shared.domain.entity;

import br.com.fiap.shared.domain.valueObject.Author;
import br.com.fiap.shared.domain.valueObject.BookName;
import br.com.fiap.shared.domain.valueObject.Description;
import br.com.fiap.shared.domain.valueObject.Email;
import br.com.fiap.shared.domain.valueObject.ISBN;
import br.com.fiap.shared.domain.valueObject.Name;

import java.time.LocalDateTime;
import java.util.UUID;

public final class Fixtures {

    public static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 9, 10, 0);

    private Fixtures() {
    }

    public static Book book(String name) {
        return Book.create(UUID.randomUUID(), new Author("Author of " + name), new BookName(name),
                new Description("About " + name), new ISBN("9788535902775"));
    }

    public static User member(String name) {
        return new LibraryMember(UUID.randomUUID(), new Name(name), new Email(name.toLowerCase().replace(' ', '.') + "@fiap.com.br"), true);
    }

    public static User inactiveMember(String name) {
        return new LibraryMember(UUID.randomUUID(), new Name(name), new Email(name.toLowerCase() + "@fiap.com.br"), false);
    }
}
