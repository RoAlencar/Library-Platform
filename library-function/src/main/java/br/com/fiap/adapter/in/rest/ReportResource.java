package br.com.fiap.adapter.in.rest;

import br.com.fiap.shared.application.dto.BorrowedBookResponse;
import br.com.fiap.shared.application.dto.MostBorrowedBookResponse;
import br.com.fiap.shared.application.dto.ReportResponse;
import br.com.fiap.shared.application.port.in.ReportUseCase;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Reports", description = "Library reports")
public class ReportResource {

    private final ReportUseCase reportUseCase;

    public ReportResource(ReportUseCase reportUseCase) {
        this.reportUseCase = reportUseCase;
    }

    @GET
    @Path("/most-borrowed")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Top 20 most borrowed books")
    public ReportResponse<MostBorrowedBookResponse> mostBorrowed() {
        return reportUseCase.mostBorrowedBooks();
    }

    @GET
    @Path("/active-loans")
    @Transactional(Transactional.TxType.SUPPORTS)
    @Operation(summary = "Books currently on loan with expected return date", description = "Ordered by expected return date.")
    public ReportResponse<BorrowedBookResponse> activeLoans() {
        return reportUseCase.activeLoans();
    }
}
