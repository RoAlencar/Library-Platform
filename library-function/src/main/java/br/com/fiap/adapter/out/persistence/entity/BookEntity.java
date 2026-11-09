package br.com.fiap.adapter.out.persistence.entity;

import br.com.fiap.shared.domain.valueObject.Availability;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.UUID;

@Entity
@Table(name = "books", indexes = {
        @Index(name = "idx_books_name", columnList = "name"),
        @Index(name = "idx_books_author", columnList = "author")
})
public class BookEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String author;

    @Column(length = 2000)
    public String description;

    @Column(nullable = false, unique = true, length = 13)
    public String isbn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public Availability availability;

    @Version
    public Long version;
}
