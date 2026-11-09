package br.com.fiap.shared.application.dto;

import br.com.fiap.shared.common.DateUtils;
import br.com.fiap.shared.domain.entity.Loan;
import br.com.fiap.shared.domain.valueObject.LoanStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LoanResponse(
        UUID id,
        UUID userId,
        String userName,
        UUID bookId,
        String bookName,
        String isbn,
        OffsetDateTime loanDate,
        OffsetDateTime dueDate,
        OffsetDateTime returnDate,
        LoanStatus status,
        boolean overdue,
        long daysOverdue) {

    public static LoanResponse from(Loan loan, LocalDateTime now) {
        return new LoanResponse(
                loan.getId(),
                loan.getUser().getId(),
                loan.getUser().getName().value(),
                loan.getBook().getId(),
                loan.getBook().getBookName().value(),
                loan.getBook().getIsbn().value(),
                DateUtils.toOffsetDateTime(loan.getLoanDate()),
                DateUtils.toOffsetDateTime(loan.getDueDate()),
                DateUtils.toOffsetDateTime(loan.getReturnDate()),
                loan.statusAt(now),
                loan.isOverdue(now) || loan.wasReturnedLate(),
                loan.daysOverdue(now));
    }
}
