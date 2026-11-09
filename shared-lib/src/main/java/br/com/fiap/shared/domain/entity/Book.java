package br.com.fiap.shared.domain.entity;

import br.com.fiap.shared.domain.exception.BookNotAvailableException;
import br.com.fiap.shared.domain.exception.InvalidBookStateException;
import br.com.fiap.shared.domain.valueObject.Author;
import br.com.fiap.shared.domain.valueObject.Availability;
import br.com.fiap.shared.domain.valueObject.BookName;
import br.com.fiap.shared.domain.valueObject.Description;
import br.com.fiap.shared.domain.valueObject.ISBN;

import java.util.Objects;
import java.util.UUID;

public class Book {

    private final UUID id;
    private Author author;
    private BookName bookName;
    private Description description;
    private ISBN isbn;
    private Availability availability;

    public Book(UUID id, Author author, BookName bookName, Description description, ISBN isbn, Availability availability) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.author = Objects.requireNonNull(author, "author must not be null");
        this.bookName = Objects.requireNonNull(bookName, "bookName must not be null");
        this.description = description;
        this.isbn = Objects.requireNonNull(isbn, "isbn must not be null");
        this.availability = availability == null ? Availability.AVAILABLE : availability;
    }

    public static Book create(UUID id, Author author, BookName bookName, Description description, ISBN isbn) {
        return new Book(id, author, bookName, description, isbn, Availability.AVAILABLE);
    }

    public void updateDetails(Author author, BookName bookName, Description description, ISBN isbn) {
        this.author = Objects.requireNonNull(author, "author must not be null");
        this.bookName = Objects.requireNonNull(bookName, "bookName must not be null");
        this.description = description;
        this.isbn = Objects.requireNonNull(isbn, "isbn must not be null");
    }

    public boolean isAvailable() {
        return availability == Availability.AVAILABLE;
    }

    public void borrow() {
        if (availability != Availability.AVAILABLE) {
            throw notAvailable("borrowed");
        }
        availability = Availability.BORROWED;
    }

    public void reserve() {
        if (availability != Availability.AVAILABLE) {
            throw notAvailable("reserved");
        }
        availability = Availability.RESERVED;
    }

    /** Hands a book held for a reservation over to the reserving user. */
    public void pickUpReservation() {
        if (availability != Availability.RESERVED) {
            throw new BookNotAvailableException(
                    "Book '%s' is not held for pickup (current status: %s)".formatted(bookName.value(), availability));
        }
        availability = Availability.BORROWED;
    }

    public void returnBook() {
        if (availability != Availability.BORROWED) {
            throw new InvalidBookStateException(
                    "Book '%s' is not borrowed (current status: %s)".formatted(bookName.value(), availability));
        }
        availability = Availability.AVAILABLE;
    }

    private BookNotAvailableException notAvailable(String action) {
        return new BookNotAvailableException(
                "Book '%s' cannot be %s (current status: %s)".formatted(bookName.value(), action, availability));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
