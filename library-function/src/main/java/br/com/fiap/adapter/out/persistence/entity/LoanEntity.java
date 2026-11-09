package br.com.fiap.adapter.out.persistence.entity;

import br.com.fiap.shared.domain.valueObject.LoanStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;
import java.util.UUID;

/** Dates are stored as wall-clock times in America/Sao_Paulo (see LibraryTime). */
@Entity
@Table(name = "loans", indexes = {
        @Index(name = "idx_loans_user", columnList = "user_id"),
        @Index(name = "idx_loans_book_status", columnList = "book_id, status"),
        @Index(name = "idx_loans_status_due", columnList = "status, due_date")
})
public class LoanEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    public UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    public BookEntity book;

    @Column(name = "loan_date", nullable = false)
    public LocalDateTime loanDate;

    @Column(name = "due_date")
    public LocalDateTime dueDate;

    @Column(name = "return_date")
    public LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public LoanStatus status;

    @Version
    public Long version;
}
