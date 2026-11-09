package br.com.fiap.adapter.out.persistence.repository;

import br.com.fiap.adapter.out.persistence.entity.BookEntity;
import br.com.fiap.adapter.out.persistence.entity.LoanEntity;
import br.com.fiap.adapter.out.persistence.entity.UserEntity;
import br.com.fiap.adapter.out.persistence.mapper.LoanMapper;
import br.com.fiap.shared.application.pagination.PageQuery;
import br.com.fiap.shared.application.pagination.PageResult;
import br.com.fiap.shared.application.port.out.LoanRepository;
import br.com.fiap.shared.domain.entity.Loan;
import br.com.fiap.shared.domain.valueObject.LoanStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class LoanRepositoryImpl implements LoanRepository {

    /** Fetch user and book in the same query to avoid N+1 when mapping to the domain. */
    private static final String FETCH = "from LoanEntity l join fetch l.user join fetch l.book ";
    private static final List<LoanStatus> OPEN = List.of(LoanStatus.ACTIVE, LoanStatus.OVERDUE);

    @Override
    public Loan save(Loan loan) {
        EntityManager em = LoanEntity.getEntityManager();
        LoanEntity entity = LoanEntity.<LoanEntity>findByIdOptional(loan.getId()).orElseGet(LoanEntity::new);
        LoanMapper.copyToEntity(loan, entity,
                em.getReference(UserEntity.class, loan.getUser().getId()),
                em.getReference(BookEntity.class, loan.getBook().getId()));
        entity.persistAndFlush();
        return loan;
    }

    @Override
    public Optional<Loan> findById(UUID id) {
        return LoanEntity.<LoanEntity>find(FETCH + "where l.id = ?1", id)
                .firstResultOptional()
                .map(LoanMapper::toDomain);
    }

    @Override
    public PageResult<Loan> findByUserId(UUID userId, PageQuery page) {
        return PanachePages.fetch(
                LoanEntity.find(FETCH + "where l.user.id = ?1 order by l.loanDate desc, l.id", userId),
                LoanEntity.count("user.id", userId),
                page, LoanMapper::toDomain);
    }

    @Override
    public PageResult<Loan> findOpen(PageQuery page) {
        return PanachePages.fetch(
                LoanEntity.find(FETCH + "where l.status in ?1 order by l.dueDate, l.id", OPEN),
                LoanEntity.count("status in ?1", OPEN),
                page, LoanMapper::toDomain);
    }

    @Override
    public Optional<Loan> findReservationByBookId(UUID bookId) {
        return LoanEntity.<LoanEntity>find(FETCH + "where l.book.id = ?1 and l.status = ?2 order by l.loanDate",
                        bookId, LoanStatus.RESERVED)
                .firstResultOptional()
                .map(LoanMapper::toDomain);
    }

    @Override
    public boolean existsByBookId(UUID bookId) {
        return LoanEntity.count("book.id", bookId) > 0;
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return LoanEntity.count("user.id", userId) > 0;
    }

    @Override
    public List<Loan> findAllBorrowed() {
        return LoanEntity.<LoanEntity>find(FETCH + "where l.status <> ?1", LoanStatus.RESERVED)
                .stream().map(LoanMapper::toDomain).toList();
    }

    @Override
    public List<Loan> findAllOpen() {
        return LoanEntity.<LoanEntity>find(FETCH + "where l.status in ?1", OPEN)
                .stream().map(LoanMapper::toDomain).toList();
    }
}
