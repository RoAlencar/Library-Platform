package br.com.fiap.shared.application.port.in;

import br.com.fiap.shared.application.dto.BookFilter;
import br.com.fiap.shared.application.dto.BookRequest;
import br.com.fiap.shared.application.dto.BookResponse;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;

import java.util.UUID;

public interface BookUseCase {

    BookResponse create(BookRequest request);

    BookResponse getById(UUID id);

    PageResult<BookResponse> list(BookFilter filter, PageQuery page);

    BookResponse update(UUID id, BookRequest request);

    void delete(UUID id);
}
