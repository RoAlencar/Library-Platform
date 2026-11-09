package br.com.fiap.adapter.out.persistence.repository;

import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import java.util.List;
import java.util.function.Function;

final class PanachePages {

    private PanachePages() {
    }

    static <E, D> PageResult<D> fetch(PanacheQuery<E> query, long total, PageQuery page, Function<E, D> mapper) {
        List<D> content = query.page(Page.of(page.page(), page.size())).list().stream().map(mapper).toList();
        return PageResult.of(content, page, total);
    }

    static <E, D> PageResult<D> fetch(PanacheQuery<E> query, PageQuery page, Function<E, D> mapper) {
        return fetch(query, query.count(), page, mapper);
    }
}
