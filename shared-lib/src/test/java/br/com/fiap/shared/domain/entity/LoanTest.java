package br.com.fiap.shared.domain.entity;

import br.com.fiap.shared.domain.exception.BookNotAvailableException;
import br.com.fiap.shared.domain.exception.InactiveUserException;
import br.com.fiap.shared.domain.exception.InvalidLoanStateException;
import br.com.fiap.shared.domain.valueObject.Availability;
import br.com.fiap.shared.domain.valueObject.LoanStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.fiap.shared.domain.entity.Fixtures.NOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanTest {

    @Test
    void borrowCreatesActiveLoanDueInFourteenDays() {
        Book book = Fixtures.book("Dom Casmurro");

        Loan loan = Loan.borrow(UUID.randomUUID(), Fixtures.member("Ana Silva"), book, NOW);

        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        assertEquals(NOW, loan.getLoanDate());
        assertEquals(LocalDateTime.of(2026, 11, 23, 10, 0), loan.getDueDate());
        assertNull(loan.getReturnDate());
        assertEquals(Availability.BORROWED, book.getAvailability());
    }

    @Test
    void cannotBorrowUnavailableBook() {
        Book book = Fixtures.book("Dom Casmurro");
        Loan.borrow(UUID.randomUUID(), Fixtures.member("Ana Silva"), book, NOW);

        assertThrows(BookNotAvailableException.class,
                () -> Loan.borrow(UUID.randomUUID(), Fixtures.member("Bruno Costa"), book, NOW));
    }

    @Test
    void inactiveUserCannotBorrow() {
        Book book = Fixtures.book("Dom Casmurro");

        assertThrows(InactiveUserException.class,
                () -> Loan.borrow(UUID.randomUUID(), Fixtures.inactiveMember("Carla"), book, NOW));
        assertTrue(book.isAvailable());
    }

    @Test
    void returnMarksLoanReturnedAndReleasesBook() {
        Book book = Fixtures.book("Dom Casmurro");
        Loan loan = Loan.borrow(UUID.randomUUID(), Fixtures.member("Ana Silva"), book, NOW);

        loan.returnBook(NOW.plusDays(3));

        assertEquals(LoanStatus.RETURNED, loan.getStatus());
        assertEquals(NOW.plusDays(3), loan.getReturnDate());
        assertTrue(book.isAvailable());
        assertFalse(loan.wasReturnedLate());
        assertThrows(InvalidLoanStateException.class, () -> loan.returnBook(NOW.plusDays(4)));
    }

    @Test
    void loanBecomesOverdueAfterDueDate() {
        Loan loan = Loan.borrow(UUID.randomUUID(), Fixtures.member("Ana Silva"), Fixtures.book("Dom Casmurro"), NOW);

        assertEquals(LoanStatus.ACTIVE, loan.statusAt(loan.getDueDate()));
        assertEquals(0, loan.daysOverdue(loan.getDueDate()));

        LocalDateTime threeDaysLate = loan.getDueDate().plusDays(3);
        assertTrue(loan.isOverdue(threeDaysLate));
        assertEquals(LoanStatus.OVERDUE, loan.statusAt(threeDaysLate));
        assertEquals(3, loan.daysOverdue(threeDaysLate));
    }

    @Test
    void lateReturnIsRecorded() {
        Loan loan = Loan.borrow(UUID.randomUUID(), Fixtures.member("Ana Silva"), Fixtures.book("Dom Casmurro"), NOW);

        loan.returnBook(loan.getDueDate().plusDays(2));

        assertTrue(loan.wasReturnedLate());
        assertEquals(LoanStatus.RETURNED, loan.statusAt(NOW.plusDays(60)));
        assertEquals(2, loan.daysOverdue(NOW.plusDays(60)));
    }

    @Test
    void reservingAvailableBookHoldsItForPickup() {
        Book book = Fixtures.book("Dom Casmurro");
        User user = Fixtures.member("Ana Silva");

        Loan reservation = Loan.reserve(UUID.randomUUID(), user, book, NOW);

        assertEquals(LoanStatus.RESERVED, reservation.getStatus());
        assertNull(reservation.getDueDate());
        assertEquals(Availability.RESERVED, book.getAvailability());

        reservation.fulfillReservation(NOW.plusDays(1));

        assertEquals(LoanStatus.ACTIVE, reservation.getStatus());
        assertEquals(NOW.plusDays(15), reservation.getDueDate());
        assertEquals(Availability.BORROWED, book.getAvailability());
    }

    @Test
    void reservingBorrowedBookQueuesUntilReturn() {
        Book book = Fixtures.book("Dom Casmurro");
        Loan.borrow(UUID.randomUUID(), Fixtures.member("Ana Silva"), book, NOW);

        Loan reservation = Loan.reserve(UUID.randomUUID(), Fixtures.member("Bruno Costa"), book, NOW);

        assertEquals(LoanStatus.RESERVED, reservation.getStatus());
        assertEquals(Availability.BORROWED, book.getAvailability());
        assertThrows(BookNotAvailableException.class, () -> reservation.fulfillReservation(NOW));
    }

    @Test
    void reservationsAreNotOverdueNorCountedAsBorrowed() {
        Loan reservation = Loan.reserve(UUID.randomUUID(), Fixtures.member("Ana Silva"), Fixtures.book("Dom Casmurro"), NOW);

        assertFalse(reservation.isOverdue(NOW.plusYears(1)));
        assertFalse(reservation.wasBorrowed());
        assertThrows(InvalidLoanStateException.class, () -> reservation.returnBook(NOW));
    }
}
