package br.com.fiap.shared.application.port.in;

import br.com.fiap.shared.application.dto.LoanRequest;
import br.com.fiap.shared.application.dto.LoanResponse;
import br.com.fiap.shared.application.dto.ReservationRequest;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;

import java.util.UUID;

public interface LoanUseCase {

    /** Borrows a book, or picks up the book held for the user's reservation. */
    LoanResponse borrow(LoanRequest request);

    LoanResponse returnBook(UUID loanId);

    /** Reserves the book of the given loan for another user. */
    LoanResponse reserve(UUID loanId, ReservationRequest request);

    LoanResponse getById(UUID loanId);

    PageResult<LoanResponse> listByUser(UUID userId, PageQuery page);

    PageResult<LoanResponse> listActive(PageQuery page);
}
