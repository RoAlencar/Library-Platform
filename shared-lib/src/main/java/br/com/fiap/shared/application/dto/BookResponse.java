package br.com.fiap.shared.application.dto;

import br.com.fiap.shared.domain.entity.Book;
import br.com.fiap.shared.domain.valueObject.Availability;

import java.util.UUID;

public record BookResponse(UUID id, String name, String author, String description, String isbn,
                           Availability availability) {

    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getBookName().value(),
                book.getAuthor().name(),
                book.getDescription() == null ? null : book.getDescription().value(),
                book.getIsbn().value(),
                book.getAvailability());
    }
}
