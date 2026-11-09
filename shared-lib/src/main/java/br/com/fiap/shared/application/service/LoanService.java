package br.com.fiap.shared.application.service;

import br.com.fiap.shared.application.dto.LoanRequest;
import br.com.fiap.shared.application.dto.LoanResponse;
import br.com.fiap.shared.application.dto.ReservationRequest;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import br.com.fiap.shared.application.port.in.LoanUseCase;
import br.com.fiap.shared.application.port.out.BookRepository;
import br.com.fiap.shared.application.port.out.LoanRepository;
import br.com.fiap.shared.application.port.out.UserRepository;
import br.com.fiap.shared.domain.LibraryTime;
import br.com.fiap.shared.domain.entity.Book;
import br.com.fiap.shared.domain.entity.Loan;
import br.com.fiap.shared.domain.entity.User;
import br.com.fiap.shared.domain.exception.BookNotAvailableException;
import br.com.fiap.shared.domain.exception.BookNotFoundException;
import br.com.fiap.shared.domain.exception.InvalidLoanStateException;
import br.com.fiap.shared.domain.exception.LoanNotFoundException;
import br.com.fiap.shared.domain.exception.UserNotFoundException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class LoanService implements LoanUseCase {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, UserRepository userRepository,
                       Clock clock) {
        this.loanRepository = Objects.requireNonNull(loanRepository);
        this.bookRepository = Objects.requireNonNull(bookRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public LoanResponse borrow(LoanRequest request) {
        LocalDateTime now = now();
        User user = findUser(request.userId());
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BookNotFoundException(request.bookId()));

        Optional<Loan> ownReservation = loanRepository.findReservationByBookId(book.getId())
                .filter(reservation -> reservation.getUser().equals(user));

        Loan loan;
        if (ownReservation.isPresent()) {
            loan = ownReservation.get();
            loan.fulfillReservation(now);
        } else {
            loan = Loan.borrow(UUID.randomUUID(), user, book, now);
        }

        bookRepository.save(loan.getBook());
        return LoanResponse.from(loanRepository.save(loan), now);
    }

    @Override
    public LoanResponse returnBook(UUID loanId) {
        LocalDateTime now = now();
        Loan loan = findLoan(loanId);
        loan.returnBook(now);

        Book book = loan.getBook();
        if (loanRepository.findReservationByBookId(book.getId()).isPresent()) {
            book.reserve();
        }

        bookRepository.save(book);
        return LoanResponse.from(loanRepository.save(loan), now);
    }

    @Override
    public LoanResponse reserve(UUID loanId, ReservationRequest request) {
        LocalDateTime now = now();
        Loan loan = findLoan(loanId);
        User user = findUser(request.userId());
        Book book = loan.getBook();

        if (loan.isOpen() && loan.getUser().equals(user)) {
            throw new InvalidLoanStateException("User %s already holds this book (loan %s)".formatted(user.getId(), loanId));
        }
        if (loanRepository.findReservationByBookId(book.getId()).isPresent()) {
            throw new BookNotAvailableException("Book '%s' is already reserved".formatted(book.getBookName().value()));
        }

        Loan reservation = Loan.reserve(UUID.randomUUID(), user, book, now);
        bookRepository.save(book);
        return LoanResponse.from(loanRepository.save(reservation), now);
    }

    @Override
    public LoanResponse getById(UUID loanId) {
        return LoanResponse.from(findLoan(loanId), now());
    }

    @Override
    public PageResult<LoanResponse> listByUser(UUID userId, PageQuery page) {
        findUser(userId);
        LocalDateTime now = now();
        return loanRepository.findByUserId(userId, page).map(loan -> LoanResponse.from(loan, now));
    }

    @Override
    public PageResult<LoanResponse> listActive(PageQuery page) {
        LocalDateTime now = now();
        return loanRepository.findOpen(page).map(loan -> LoanResponse.from(loan, now));
    }

    private LocalDateTime now() {
        return LibraryTime.now(clock);
    }

    private Loan findLoan(UUID id) {
        return loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException(id));
    }

    private User findUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
