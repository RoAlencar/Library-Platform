package br.com.fiap.adapter.out.persistence.mapper;

import br.com.fiap.adapter.out.persistence.entity.BookEntity;
import br.com.fiap.shared.domain.entity.Book;
import br.com.fiap.shared.domain.valueObject.Author;
import br.com.fiap.shared.domain.valueObject.BookName;
import br.com.fiap.shared.domain.valueObject.Description;
import br.com.fiap.shared.domain.valueObject.ISBN;

public final class BookMapper {

    private BookMapper() {
    }

    public static Book toDomain(BookEntity entity) {
        return new Book(
                entity.id,
                new Author(entity.author),
                new BookName(entity.name),
                entity.description == null ? null : new Description(entity.description),
                new ISBN(entity.isbn),
                entity.availability);
    }

    public static void copyToEntity(Book book, BookEntity entity) {
        entity.id = book.getId();
        entity.name = book.getBookName().value();
        entity.author = book.getAuthor().name();
        entity.description = book.getDescription() == null ? null : book.getDescription().value();
        entity.isbn = book.getIsbn().value();
        entity.availability = book.getAvailability();
    }
}
