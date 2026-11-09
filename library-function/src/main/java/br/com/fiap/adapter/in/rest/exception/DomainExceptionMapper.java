package br.com.fiap.adapter.in.rest.exception;

import br.com.fiap.shared.common.DateUtils;
import br.com.fiap.shared.domain.exception.ConflictException;
import br.com.fiap.shared.domain.exception.DomainException;
import br.com.fiap.shared.domain.exception.DomainValidationException;
import br.com.fiap.shared.domain.exception.NotFoundException;
import br.com.fiap.shared.domain.exception.UserAlreadyExistsException;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * Maps domain and persistence exceptions to HTTP:
 * 404 not found, 409 conflicts (state/uniqueness/concurrency), 422 domain validation and business rules.
 * Bean Validation failures on request bodies/params are answered with 400 by Quarkus.
 */
public class DomainExceptionMapper {

    private static final Logger LOG = Logger.getLogger(DomainExceptionMapper.class);

    @ServerExceptionMapper
    public Response mapDomain(DomainException exception, UriInfo uriInfo) {
        Response.Status status;
        if (exception instanceof NotFoundException) {
            status = Response.Status.NOT_FOUND;
        } else if (exception instanceof ConflictException) {
            status = Response.Status.CONFLICT;
        } else {
            status = Response.Status.fromStatusCode(422);
        }
        return build(status.getStatusCode(), status.getReasonPhrase(), exception.getMessage(), uriInfo);
    }

    @ServerExceptionMapper
    public Response mapDomainValidation(DomainValidationException exception, UriInfo uriInfo) {
        if (exception instanceof UserAlreadyExistsException) {
            return build(409, "Conflict", exception.getMessage(), uriInfo);
        }
        return build(422, "Unprocessable Entity", exception.getMessage(), uriInfo);
    }

    @ServerExceptionMapper
    public Response mapOptimisticLock(OptimisticLockException exception, UriInfo uriInfo) {
        return build(409, "Conflict", "The resource was modified concurrently, please retry", uriInfo);
    }

    @ServerExceptionMapper
    public Response mapConstraintViolation(ConstraintViolationException exception, UriInfo uriInfo) {
        LOG.debugf(exception, "Database constraint violated: %s", exception.getConstraintName());
        return build(409, "Conflict", "The operation violates a data integrity constraint", uriInfo);
    }

    private static Response build(int status, String error, String message, UriInfo uriInfo) {
        ErrorResponse body = new ErrorResponse(DateUtils.toOffsetDateTime(DateUtils.now()), status, error, message,
                uriInfo.getPath());
        return Response.status(status).type(MediaType.APPLICATION_JSON).entity(body).build();
    }
}
