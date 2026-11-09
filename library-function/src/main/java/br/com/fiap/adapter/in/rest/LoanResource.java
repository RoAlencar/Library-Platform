package br.com.fiap.adapter.in.rest;

import br.com.fiap.shared.application.dto.LoanRequest;
import br.com.fiap.shared.application.dto.LoanResponse;
import br.com.fiap.shared.application.dto.ReservationRequest;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import br.com.fiap.shared.application.port.in.LoanUseCase;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.UUID;

@Path("/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Loans", description = "Borrowing, returning and reserving books. Dates are ISO-8601 in America/Sao_Paulo (-03:00).")
public class LoanResource {

    private final LoanUseCase loanUseCase;

    public LoanResource(LoanUseCase loanUseCase) {
        this.loanUseCase = loanUseCase;
    }

    @POST
    @Transactional
    @Operation(summary = "Borrow a book",
            description = "Creates an ACTIVE loan due in 14 days. If the book is held for this user's reservation, the reservation is picked up.")
    public RestResponse<LoanResponse> borrow(@Valid @NotNull LoanRequest request, UriInfo uriInfo) {
        LoanResponse loan = loanUseCase.borrow(request);
        return Responses.created(uriInfo.getBaseUriBuilder().path("loans").path(loan.id().toString()).build(), loan);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a loan by id")
    public LoanResponse get(@PathParam("id") UUID id) {
        return loanUseCase.getById(id);
    }

    @POST
    @Path("/{id}/return")
    @Consumes(MediaType.WILDCARD)
    @Transactional
    @Operation(summary = "Return a borrowed book",
            description = "Marks the loan RETURNED. If another user reserved the book, it becomes RESERVED for them; otherwise AVAILABLE.")
    public LoanResponse returnBook(@PathParam("id") UUID id) {
        return loanUseCase.returnBook(id);
    }

    @POST
    @Path("/{id}/reserve")
    @Transactional
    @Operation(summary = "Reserve the book of a loan for another user",
            description = "Creates a RESERVED loan. A borrowed book is held for the user when returned; an available book is held immediately. One reservation per book.")
    public RestResponse<LoanResponse> reserve(@PathParam("id") UUID id, @Valid @NotNull ReservationRequest request,
                                              UriInfo uriInfo) {
        LoanResponse reservation = loanUseCase.reserve(id, request);
        return Responses.created(
                uriInfo.getBaseUriBuilder().path("loans").path(reservation.id().toString()).build(), reservation);
    }

    @GET
    @Path("/active")
    @Operation(summary = "List active loans", description = "Books currently with users (ACTIVE or OVERDUE), ordered by due date.")
    public PageResult<LoanResponse> active(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("10") @Min(1) @Max(PageQuery.MAX_SIZE) int size) {
        return loanUseCase.listActive(PageQuery.of(page, size));
    }

    @GET
    @Path("/user/{userId}")
    @Operation(summary = "List loans of a user", description = "Same as GET /users/{id}/loans.")
    public PageResult<LoanResponse> byUser(
            @PathParam("userId") UUID userId,
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("10") @Min(1) @Max(PageQuery.MAX_SIZE) int size) {
        return loanUseCase.listByUser(userId, PageQuery.of(page, size));
    }
}
