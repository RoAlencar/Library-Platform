package br.com.fiap.shared.application.port.out;

import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import br.com.fiap.shared.domain.entity.Loan;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository {

    Loan save(Loan loan);

    Optional<Loan> findById(UUID id);

    /** Full history of a user, newest first. */
    PageResult<Loan> findByUserId(UUID userId, PageQuery page);

    /** Loans whose book is currently with a user (ACTIVE/OVERDUE), ordered by due date. */
    PageResult<Loan> findOpen(PageQuery page);

    /** Pending reservation (status RESERVED) for the given book, if any. */
    Optional<Loan> findReservationByBookId(UUID bookId);

    boolean existsByBookId(UUID bookId);

    boolean existsByUserId(UUID userId);

    /** Report query: every loan in which the book actually left the library (not RESERVED). */
    List<Loan> findAllBorrowed();

    /** Report query: every loan whose book is currently with a user. */
    List<Loan> findAllOpen();
}
