package br.com.fiap.shared.application.service;

import br.com.fiap.shared.application.dto.BookFilter;
import br.com.fiap.shared.application.dto.BookRequest;
import br.com.fiap.shared.application.dto.BookResponse;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import br.com.fiap.shared.application.port.in.BookUseCase;
import br.com.fiap.shared.application.port.out.BookRepository;
import br.com.fiap.shared.application.port.out.LoanRepository;
import br.com.fiap.shared.domain.entity.Book;
import br.com.fiap.shared.domain.exception.BookAlreadyExistsException;
import br.com.fiap.shared.domain.exception.BookNotFoundException;
import br.com.fiap.shared.domain.exception.ResourceInUseException;
import br.com.fiap.shared.domain.valueObject.Author;
import br.com.fiap.shared.domain.valueObject.BookName;
import br.com.fiap.shared.domain.valueObject.Description;
import br.com.fiap.shared.domain.valueObject.ISBN;

import java.util.Objects;
import java.util.UUID;

public class BookService implements BookUseCase {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public BookService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = Objects.requireNonNull(bookRepository);
        this.loanRepository = Objects.requireNonNull(loanRepository);
    }

    @Override
    public BookResponse create(BookRequest request) {
        ISBN isbn = new ISBN(request.isbn());
        ensureIsbnIsFree(isbn, null);
        Book book = Book.create(UUID.randomUUID(), new Author(request.author()), new BookName(request.name()),
                description(request), isbn);
        return BookResponse.from(bookRepository.save(book));
    }

    @Override
    public BookResponse getById(UUID id) {
        return BookResponse.from(findBook(id));
    }

    @Override
    public PageResult<BookResponse> list(BookFilter filter, PageQuery page) {
        return bookRepository.findAll(filter == null ? BookFilter.empty() : filter, page).map(BookResponse::from);
    }

    @Override
    public BookResponse update(UUID id, BookRequest request) {
        Book book = findBook(id);
        ISBN isbn = new ISBN(request.isbn());
        ensureIsbnIsFree(isbn, id);
        book.updateDetails(new Author(request.author()), new BookName(request.name()), description(request), isbn);
        return BookResponse.from(bookRepository.save(book));
    }

    @Override
    public void delete(UUID id) {
        findBook(id);
        if (loanRepository.existsByBookId(id)) {
            throw new ResourceInUseException("Book %s has loan history and cannot be deleted".formatted(id));
        }
        bookRepository.deleteById(id);
    }

    private Book findBook(UUID id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    private void ensureIsbnIsFree(ISBN isbn, UUID currentBookId) {
        bookRepository.findByIsbn(isbn.value())
                .filter(existing -> !existing.getId().equals(currentBookId))
                .ifPresent(existing -> {
                    throw new BookAlreadyExistsException("A book with ISBN %s already exists".formatted(isbn.value()));
                });
    }

    private static Description description(BookRequest request) {
        return request.description() == null || request.description().isBlank()
                ? null
                : new Description(request.description());
    }
}
