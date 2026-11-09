package br.com.fiap.shared.domain.exception;

import java.util.UUID;

public class LoanNotFoundException extends NotFoundException {
    public LoanNotFoundException(UUID id) {
        super("Loan not found: " + id);
    }
}
