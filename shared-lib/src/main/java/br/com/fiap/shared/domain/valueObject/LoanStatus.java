package br.com.fiap.shared.domain.valueObject;

public enum LoanStatus {
    /** Book is held (or queued) for the user, waiting for pickup. */
    RESERVED,
    /** Book is with the user and within the due date. */
    ACTIVE,
    /** Book is with the user and past the due date (derived from ACTIVE at read time). */
    OVERDUE,
    /** Book was given back. */
    RETURNED
}
