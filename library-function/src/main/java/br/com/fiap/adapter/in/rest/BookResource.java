package br.com.fiap.adapter.in.rest;

import br.com.fiap.shared.application.dto.BookFilter;
import br.com.fiap.shared.application.dto.BookRequest;
import br.com.fiap.shared.application.dto.BookResponse;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import br.com.fiap.shared.application.port.in.BookUseCase;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.UUID;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Books", description = "Book catalogue management")
public class BookResource {

    private final BookUseCase bookUseCase;

    public BookResource(BookUseCase bookUseCase) {
        this.bookUseCase = bookUseCase;
    }

    @POST
    @Transactional
    @Operation(summary = "Register a book", description = "ISBN must be unique; new books start AVAILABLE.")
    public RestResponse<BookResponse> create(@Valid @NotNull BookRequest request, UriInfo uriInfo) {
        BookResponse created = bookUseCase.create(request);
        return Responses.created(uriInfo.getAbsolutePathBuilder().path(created.id().toString()).build(), created);
    }

    @GET
    @Operation(summary = "List books", description = "Paginated list filtered by name/author (partial, case-insensitive) and ISBN (exact).")
    public PageResult<BookResponse> list(
            @Parameter(description = "Part of the book name") @QueryParam("name") String name,
            @Parameter(description = "Part of the author name") @QueryParam("author") String author,
            @Parameter(description = "ISBN-10 or ISBN-13, hyphens allowed") @QueryParam("isbn") String isbn,
            @Parameter(description = "Zero-based page index") @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @Parameter(description = "Page size (1-100)") @QueryParam("size") @DefaultValue("10") @Min(1) @Max(PageQuery.MAX_SIZE) int size) {
        return bookUseCase.list(new BookFilter(name, author, isbn), PageQuery.of(page, size));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a book by id")
    public BookResponse get(@PathParam("id") UUID id) {
        return bookUseCase.getById(id);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update book details", description = "Availability is managed by loans and is not changed here.")
    public BookResponse update(@PathParam("id") UUID id, @Valid @NotNull BookRequest request) {
        return bookUseCase.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a book", description = "Books with loan history cannot be deleted (409).")
    public RestResponse<Void> delete(@PathParam("id") UUID id) {
        bookUseCase.delete(id);
        return RestResponse.noContent();
    }
}
