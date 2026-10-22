package br.com.fiap.shared.domain.entity;

import br.com.fiap.shared.domain.valueObject.Author;
import br.com.fiap.shared.domain.valueObject.Availability;
import br.com.fiap.shared.domain.valueObject.BookName;
import br.com.fiap.shared.domain.valueObject.Description;
import br.com.fiap.shared.domain.valueObject.ISBN;

import java.util.UUID;

public class Book {

    private final UUID id;
    private final Author author;
    private final BookName bookName;
    private final Description description;
    private final ISBN isbn;
    private final Availability availability;

    public Book(UUID id, Author author, BookName bookName, Description description, ISBN isbn, Availability availability) {
        this.id = id;
        this.author = author;
        this.bookName = bookName;
        this.description = description;
        this.isbn = isbn;
        this.availability = availability;
    }

    public UUID getId() {
        return id;
    }

    public Author getAuthor() {
        return author;
    }

    public BookName getBookName() {
        return bookName;
    }

    public Description getDescription() {
        return description;
    }

    public ISBN getIsbn() {
        return isbn;
    }

    public Availability getAvailability() {
        return availability;
    }
}
