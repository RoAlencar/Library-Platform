package br.com.fiap.shared.application.dto;

import br.com.fiap.shared.common.DateUtils;
import br.com.fiap.shared.domain.entity.LibraryReport;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BorrowedBookResponse(
        UUID loanId,
        UUID bookId,
        String bookName,
        String author,
        String isbn,
        UUID userId,
        String userName,
        OffsetDateTime loanDate,
        OffsetDateTime expectedReturnDate,
        boolean overdue,
        long daysOverdue) {

    public static BorrowedBookResponse from(LibraryReport.BorrowedBook borrowed) {
        var loan = borrowed.loan();
        var book = loan.getBook();
        return new BorrowedBookResponse(
                loan.getId(),
                book.getId(),
                book.getBookName().value(),
                book.getAuthor().name(),
                book.getIsbn().value(),
                loan.getUser().getId(),
                loan.getUser().getName().value(),
                DateUtils.toOffsetDateTime(loan.getLoanDate()),
                DateUtils.toOffsetDateTime(borrowed.expectedReturnDate()),
                borrowed.overdue(),
                borrowed.daysOverdue());
    }
}
