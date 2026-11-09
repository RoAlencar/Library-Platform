package br.com.fiap.shared.application.port.in;

import br.com.fiap.shared.application.dto.BorrowedBookResponse;
import br.com.fiap.shared.application.dto.MostBorrowedBookResponse;
import br.com.fiap.shared.application.dto.ReportResponse;

public interface ReportUseCase {

    ReportResponse<MostBorrowedBookResponse> mostBorrowedBooks();

    ReportResponse<BorrowedBookResponse> activeLoans();
}
