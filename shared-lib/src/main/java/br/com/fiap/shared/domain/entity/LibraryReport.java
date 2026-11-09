package br.com.fiap.shared.domain.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LibraryReport {

    public static final int MOST_BORROWED_LIMIT = 20;

    private final List<Loan> loans;
    private final LocalDateTime generatedAt;

    public LibraryReport(Collection<Loan> loans, LocalDateTime generatedAt) {
        this.loans = List.copyOf(Objects.requireNonNull(loans, "loans must not be null"));
        this.generatedAt = Objects.requireNonNull(generatedAt, "generatedAt must not be null");
    }

    public List<BookBorrowCount> mostBorrowedBooks() {
        return mostBorrowedBooks(MOST_BORROWED_LIMIT);
    }

    public List<BookBorrowCount> mostBorrowedBooks(int limit) {
        Map<Book, Long> countByBook = loans.stream()
                .filter(Loan::wasBorrowed)
                .collect(Collectors.groupingBy(Loan::getBook, Collectors.counting()));

        return countByBook.entrySet().stream()
                .map(entry -> new BookBorrowCount(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(BookBorrowCount::timesBorrowed).reversed()
                        .thenComparing(count -> count.book().getBookName().value(), String.CASE_INSENSITIVE_ORDER))
                .limit(limit)
                .toList();
    }

    public List<BorrowedBook> currentlyBorrowedBooks() {
        return loans.stream()
                .filter(Loan::isOpen)
                .sorted(Comparator.comparing(Loan::getDueDate))
                .map(toBorrowedBook())
                .toList();
    }

    private Function<Loan, BorrowedBook> toBorrowedBook() {
        return loan -> new BorrowedBook(loan, loan.getDueDate(), loan.isOverdue(generatedAt), loan.daysOverdue(generatedAt));
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public record BookBorrowCount(Book book, long timesBorrowed) {
    }

    public record BorrowedBook(Loan loan, LocalDateTime expectedReturnDate, boolean overdue, long daysOverdue) {
    }
}
