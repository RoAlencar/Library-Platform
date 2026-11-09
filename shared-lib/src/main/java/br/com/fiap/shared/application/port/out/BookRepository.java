package br.com.fiap.shared.application.port.out;

import br.com.fiap.shared.application.dto.BookFilter;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import br.com.fiap.shared.domain.entity.Book;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(UUID id);

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    /** Filters by name/author (case-insensitive, partial) and ISBN (exact); null filter fields are ignored. */
    PageResult<Book> findAll(BookFilter filter, PageQuery page);

    void deleteById(UUID id);
}
