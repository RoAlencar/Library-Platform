package br.com.fiap.shared.application.dto;

import br.com.fiap.shared.domain.entity.LibraryReport;

import java.util.UUID;

public record MostBorrowedBookResponse(int rank, UUID bookId, String name, String author, String isbn,
                                       long timesBorrowed) {

    public static MostBorrowedBookResponse from(int rank, LibraryReport.BookBorrowCount count) {
        var book = count.book();
        return new MostBorrowedBookResponse(rank, book.getId(), book.getBookName().value(), book.getAuthor().name(),
                book.getIsbn().value(), count.timesBorrowed());
    }
}
