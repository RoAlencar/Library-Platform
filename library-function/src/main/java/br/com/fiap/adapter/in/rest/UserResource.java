package br.com.fiap.adapter.in.rest;

import br.com.fiap.shared.application.dto.LoanResponse;
import br.com.fiap.shared.application.dto.UserRequest;
import br.com.fiap.shared.application.dto.UserResponse;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import br.com.fiap.shared.application.port.in.UserUseCase;
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
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.UUID;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "Library members and librarians")
public class UserResource {

    private final UserUseCase userUseCase;

    public UserResource(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @POST
    @Transactional
    @Operation(summary = "Register a user", description = "Email must be unique. type: LIBRARY_MEMBER (default) or LIBRARIAN.")
    public RestResponse<UserResponse> create(@Valid @NotNull UserRequest request, UriInfo uriInfo) {
        UserResponse created = userUseCase.create(request);
        return Responses.created(uriInfo.getAbsolutePathBuilder().path(created.id().toString()).build(), created);
    }

    @GET
    @Operation(summary = "List users (paginated)")
    public PageResult<UserResponse> list(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("10") @Min(1) @Max(PageQuery.MAX_SIZE) int size) {
        return userUseCase.list(PageQuery.of(page, size));
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a user by id")
    public UserResponse get(@PathParam("id") UUID id) {
        return userUseCase.getById(id);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update a user")
    public UserResponse update(@PathParam("id") UUID id, @Valid @NotNull UserRequest request) {
        return userUseCase.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a user", description = "Users with loan history cannot be deleted (409); deactivate them instead.")
    public RestResponse<Void> delete(@PathParam("id") UUID id) {
        userUseCase.delete(id);
        return RestResponse.noContent();
    }

    @GET
    @Path("/{id}/loans")
    @Operation(summary = "Loan history of a user", description = "All loans and reservations of the user, newest first.")
    public PageResult<LoanResponse> loans(
            @PathParam("id") UUID id,
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("10") @Min(1) @Max(PageQuery.MAX_SIZE) int size) {
        return userUseCase.loanHistory(id, PageQuery.of(page, size));
    }
}
