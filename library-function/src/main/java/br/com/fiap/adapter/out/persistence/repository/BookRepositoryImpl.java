package br.com.fiap.adapter.out.persistence.repository;

import br.com.fiap.adapter.out.persistence.entity.BookEntity;
import br.com.fiap.adapter.out.persistence.mapper.BookMapper;
import br.com.fiap.shared.application.dto.BookFilter;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import br.com.fiap.shared.application.port.out.BookRepository;
import br.com.fiap.shared.domain.entity.Book;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class BookRepositoryImpl implements BookRepository {

    @Override
    public Book save(Book book) {
        BookEntity entity = BookEntity.<BookEntity>findByIdOptional(book.getId()).orElseGet(BookEntity::new);
        BookMapper.copyToEntity(book, entity);
        entity.persistAndFlush();
        return BookMapper.toDomain(entity);
    }

    @Override
    public Optional<Book> findById(UUID id) {
        return BookEntity.<BookEntity>findByIdOptional(id).map(BookMapper::toDomain);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return BookEntity.<BookEntity>find("isbn", isbn).firstResultOptional().map(BookMapper::toDomain);
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return BookEntity.count("isbn", isbn) > 0;
    }

    @Override
    public PageResult<Book> findAll(BookFilter filter, PageQuery page) {
        List<String> conditions = new ArrayList<>();
        Parameters params = new Parameters();
        if (filter.name() != null) {
            conditions.add("lower(name) like :name");
            params.and("name", likePattern(filter.name()));
        }
        if (filter.author() != null) {
            conditions.add("lower(author) like :author");
            params.and("author", likePattern(filter.author()));
        }
        if (filter.isbn() != null) {
            conditions.add("isbn = :isbn");
            params.and("isbn", filter.isbn());
        }

        Sort sort = Sort.by("name").and("id");
        PanacheQuery<BookEntity> query = conditions.isEmpty()
                ? BookEntity.findAll(sort)
                : BookEntity.find(String.join(" and ", conditions), sort, params);
        return PanachePages.fetch(query, page, BookMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        BookEntity.deleteById(id);
    }

    private static String likePattern(String value) {
        return "%" + value.toLowerCase(Locale.ROOT) + "%";
    }
}
