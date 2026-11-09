package br.com.fiap.shared.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static br.com.fiap.shared.domain.entity.Fixtures.NOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryReportTest {

    private final User user = Fixtures.member("Ana Silva");

    @Test
    void mostBorrowedBooksAreRankedByBorrowCountAndLimitedToTwenty() {
        List<Loan> loans = new ArrayList<>();
        List<Book> books = IntStream.rangeClosed(1, 25).mapToObj(i -> Fixtures.book("Book %02d".formatted(i))).toList();
        for (int i = 0; i < books.size(); i++) {
            for (int times = 0; times <= i; times++) {
                loans.add(returnedLoan(books.get(i)));
            }
        }
        loans.add(Loan.reserve(UUID.randomUUID(), user, books.get(0), NOW));

        List<LibraryReport.BookBorrowCount> ranking = new LibraryReport(loans, NOW).mostBorrowedBooks();

        assertEquals(LibraryReport.MOST_BORROWED_LIMIT, ranking.size());
        assertEquals("Book 25", ranking.get(0).book().getBookName().value());
        assertEquals(25, ranking.get(0).timesBorrowed());
        assertEquals("Book 06", ranking.get(19).book().getBookName().value());
        assertEquals(6, ranking.get(19).timesBorrowed());
    }

    @Test
    void tiesAreOrderedByBookName() {
        Book zeta = Fixtures.book("Zeta");
        Book alpha = Fixtures.book("alpha");
        List<Loan> loans = List.of(returnedLoan(zeta), returnedLoan(alpha));

        List<LibraryReport.BookBorrowCount> ranking = new LibraryReport(loans, NOW).mostBorrowedBooks();

        assertEquals(List.of(alpha, zeta), ranking.stream().map(LibraryReport.BookBorrowCount::book).toList());
    }

    @Test
    void currentlyBorrowedBooksListsOpenLoansByExpectedReturnDate() {
        Loan older = Loan.borrow(UUID.randomUUID(), user, Fixtures.book("Older"), NOW.minusDays(20));
        Loan recent = Loan.borrow(UUID.randomUUID(), user, Fixtures.book("Recent"), NOW.minusDays(1));
        Loan returned = returnedLoan(Fixtures.book("Returned"));
        Loan reservation = Loan.reserve(UUID.randomUUID(), user, Fixtures.book("Reserved"), NOW);

        List<LibraryReport.BorrowedBook> borrowed =
                new LibraryReport(List.of(recent, returned, reservation, older), NOW).currentlyBorrowedBooks();

        assertEquals(List.of(older, recent), borrowed.stream().map(LibraryReport.BorrowedBook::loan).toList());
        assertEquals(NOW.minusDays(6), borrowed.get(0).expectedReturnDate());
        assertTrue(borrowed.get(0).overdue());
        assertEquals(6, borrowed.get(0).daysOverdue());
        assertFalse(borrowed.get(1).overdue());
        assertEquals(NOW.plusDays(13), borrowed.get(1).expectedReturnDate());
    }

    private Loan returnedLoan(Book book) {
        Loan loan = Loan.borrow(UUID.randomUUID(), user, book, NOW.minusDays(10));
        loan.returnBook(NOW.minusDays(5));
        return loan;
    }
}
