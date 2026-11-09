package br.com.fiap.config;

import br.com.fiap.shared.application.port.in.BookUseCase;
import br.com.fiap.shared.application.port.in.LoanUseCase;
import br.com.fiap.shared.application.port.in.ReportUseCase;
import br.com.fiap.shared.application.port.in.UserUseCase;
import br.com.fiap.shared.application.port.out.BookRepository;
import br.com.fiap.shared.application.port.out.LoanRepository;
import br.com.fiap.shared.application.port.out.UserRepository;
import br.com.fiap.shared.application.service.BookService;
import br.com.fiap.shared.application.service.LoanService;
import br.com.fiap.shared.application.service.ReportService;
import br.com.fiap.shared.application.service.UserService;
import br.com.fiap.shared.domain.LibraryTime;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.time.Clock;

/** Wires the framework-agnostic use cases from shared-lib to the Quarkus adapters. */
@ApplicationScoped
public class UseCaseConfiguration {

    @Produces
    @ApplicationScoped
    Clock clock() {
        return LibraryTime.systemClock();
    }

    @Produces
    @ApplicationScoped
    BookUseCase bookUseCase(BookRepository bookRepository, LoanRepository loanRepository) {
        return new BookService(bookRepository, loanRepository);
    }

    @Produces
    @ApplicationScoped
    UserUseCase userUseCase(UserRepository userRepository, LoanRepository loanRepository, Clock clock) {
        return new UserService(userRepository, loanRepository, clock);
    }

    @Produces
    @ApplicationScoped
    LoanUseCase loanUseCase(LoanRepository loanRepository, BookRepository bookRepository,
                            UserRepository userRepository, Clock clock) {
        return new LoanService(loanRepository, bookRepository, userRepository, clock);
    }

    @Produces
    @ApplicationScoped
    ReportUseCase reportUseCase(LoanRepository loanRepository, Clock clock) {
        return new ReportService(loanRepository, clock);
    }
}
