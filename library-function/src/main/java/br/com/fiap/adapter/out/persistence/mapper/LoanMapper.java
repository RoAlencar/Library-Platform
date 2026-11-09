package br.com.fiap.adapter.out.persistence.mapper;

import br.com.fiap.adapter.out.persistence.entity.BookEntity;
import br.com.fiap.adapter.out.persistence.entity.LoanEntity;
import br.com.fiap.adapter.out.persistence.entity.UserEntity;
import br.com.fiap.shared.domain.entity.Loan;

public final class LoanMapper {

    private LoanMapper() {
    }

    public static Loan toDomain(LoanEntity entity) {
        return Loan.restore(
                entity.id,
                UserMapper.toDomain(entity.user),
                BookMapper.toDomain(entity.book),
                entity.loanDate,
                entity.dueDate,
                entity.returnDate,
                entity.status);
    }

    public static void copyToEntity(Loan loan, LoanEntity entity, UserEntity user, BookEntity book) {
        entity.id = loan.getId();
        entity.user = user;
        entity.book = book;
        entity.loanDate = loan.getLoanDate();
        entity.dueDate = loan.getDueDate();
        entity.returnDate = loan.getReturnDate();
        entity.status = loan.getStatus();
    }
}
