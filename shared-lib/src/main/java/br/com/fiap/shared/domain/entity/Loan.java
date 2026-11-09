package br.com.fiap.shared.domain.entity;

import br.com.fiap.shared.domain.exception.InactiveUserException;
import br.com.fiap.shared.domain.exception.InvalidLoanStateException;
import br.com.fiap.shared.domain.valueObject.Availability;
import br.com.fiap.shared.domain.valueObject.LoanStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

/**
 * Loan aggregate. All dates are wall-clock times in {@link br.com.fiap.shared.domain.LibraryTime#ZONE_ID}.
 * A loan starts either ACTIVE (immediate borrow) or RESERVED (hold/queue), becomes ACTIVE on pickup
 * and RETURNED when the book is given back. OVERDUE is derived from ACTIVE once the due date has passed.
 */
public class Loan {

    public static final int DEFAULT_LOAN_PERIOD_DAYS = 14;

    private final UUID id;
    private final User user;
    private final Book book;
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private LoanStatus status;

    private Loan(UUID id, User user, Book book, LocalDateTime loanDate, LocalDateTime dueDate,
                 LocalDateTime returnDate, LoanStatus status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.user = Objects.requireNonNull(user, "user must not be null");
        this.book = Objects.requireNonNull(book, "book must not be null");
        this.loanDate = Objects.requireNonNull(loanDate, "loanDate must not be null");
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public static Loan borrow(UUID id, User user, Book book, LocalDateTime now) {
        requireActive(user);
        book.borrow();
        return new Loan(id, user, book, now, calculateDueDate(now), null, LoanStatus.ACTIVE);
    }

    /**
     * Reserves a book. An available book is held immediately for the user; a borrowed book is queued
     * and will be held for the user when it is returned.
     */
    public static Loan reserve(UUID id, User user, Book book, LocalDateTime now) {
        requireActive(user);
        if (book.getAvailability() != Availability.BORROWED) {
            book.reserve();
        }
        return new Loan(id, user, book, now, null, null, LoanStatus.RESERVED);
    }

    public static Loan restore(UUID id, User user, Book book, LocalDateTime loanDate, LocalDateTime dueDate,
                               LocalDateTime returnDate, LoanStatus status) {
        return new Loan(id, user, book, loanDate, dueDate, returnDate, status);
    }

    public static LocalDateTime calculateDueDate(LocalDateTime loanDate) {
        return loanDate.plusDays(DEFAULT_LOAN_PERIOD_DAYS);
    }

    public void fulfillReservation(LocalDateTime now) {
        if (status != LoanStatus.RESERVED) {
            throw new InvalidLoanStateException("Loan %s is not a reservation (status: %s)".formatted(id, status));
        }
        requireActive(user);
        book.pickUpReservation();
        this.loanDate = now;
        this.dueDate = calculateDueDate(now);
        this.status = LoanStatus.ACTIVE;
    }

    public void returnBook(LocalDateTime now) {
        if (!isOpen()) {
            throw new InvalidLoanStateException("Loan %s cannot be returned (status: %s)".formatted(id, status));
        }
        book.returnBook();
        this.returnDate = now;
        this.status = LoanStatus.RETURNED;
    }

    /** Book is currently with the user. */
    public boolean isOpen() {
        return status == LoanStatus.ACTIVE || status == LoanStatus.OVERDUE;
    }

    public boolean isReservation() {
        return status == LoanStatus.RESERVED;
    }

    /** The book actually left the library (counts for "most borrowed" statistics). */
    public boolean wasBorrowed() {
        return status != LoanStatus.RESERVED;
    }

    public boolean isOverdue(LocalDateTime now) {
        return isOpen() && now.isAfter(dueDate);
    }

    public boolean wasReturnedLate() {
        return status == LoanStatus.RETURNED && returnDate.isAfter(dueDate);
    }

    public LoanStatus statusAt(LocalDateTime now) {
        return isOverdue(now) ? LoanStatus.OVERDUE : status;
    }

    public long daysOverdue(LocalDateTime now) {
        if (dueDate == null || status == LoanStatus.RESERVED) {
            return 0;
        }
        LocalDateTime reference = returnDate != null ? returnDate : now;
        if (!reference.isAfter(dueDate)) {
            return 0;
        }
        return Math.max(1, ChronoUnit.DAYS.between(dueDate.toLocalDate(), reference.toLocalDate()));
    }

    private static void requireActive(User user) {
        if (!user.isActive()) {
            throw new InactiveUserException(user.getId());
        }
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loan other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
