package br.com.fiap.shared.application.service;

import br.com.fiap.shared.application.dto.BorrowedBookResponse;
import br.com.fiap.shared.application.dto.MostBorrowedBookResponse;
import br.com.fiap.shared.application.dto.ReportResponse;
import br.com.fiap.shared.application.port.in.ReportUseCase;
import br.com.fiap.shared.application.port.out.LoanRepository;
import br.com.fiap.shared.domain.LibraryTime;
import br.com.fiap.shared.domain.entity.LibraryReport;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class ReportService implements ReportUseCase {

    private final LoanRepository loanRepository;
    private final Clock clock;

    public ReportService(LoanRepository loanRepository, Clock clock) {
        this.loanRepository = Objects.requireNonNull(loanRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public ReportResponse<MostBorrowedBookResponse> mostBorrowedBooks() {
        LocalDateTime now = LibraryTime.now(clock);
        List<LibraryReport.BookBorrowCount> ranking = new LibraryReport(loanRepository.findAllBorrowed(), now)
                .mostBorrowedBooks();
        List<MostBorrowedBookResponse> items = IntStream.range(0, ranking.size())
                .mapToObj(i -> MostBorrowedBookResponse.from(i + 1, ranking.get(i)))
                .toList();
        return ReportResponse.of("Top %d most borrowed books".formatted(LibraryReport.MOST_BORROWED_LIMIT), now, items);
    }

    @Override
    public ReportResponse<BorrowedBookResponse> activeLoans() {
        LocalDateTime now = LibraryTime.now(clock);
        List<BorrowedBookResponse> items = new LibraryReport(loanRepository.findAllOpen(), now)
                .currentlyBorrowedBooks().stream()
                .map(BorrowedBookResponse::from)
                .toList();
        return ReportResponse.of("Books currently on loan with expected return date", now, items);
    }
}
