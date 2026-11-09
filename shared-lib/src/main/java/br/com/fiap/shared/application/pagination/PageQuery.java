package br.com.fiap.shared.application.pagination;

import br.com.fiap.shared.domain.exception.DomainValidationException;

public record PageQuery(int page, int size) {

    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_SIZE = 100;

    public PageQuery {
        if (page < 0) {
            throw new DomainValidationException("page must be greater than or equal to 0");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new DomainValidationException("size must be between 1 and " + MAX_SIZE);
        }
    }

    public static PageQuery of(int page, int size) {
        return new PageQuery(page, size);
    }
}
