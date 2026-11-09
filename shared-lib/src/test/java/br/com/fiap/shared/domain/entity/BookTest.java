package br.com.fiap.shared.domain.entity;

import br.com.fiap.shared.domain.exception.BookNotAvailableException;
import br.com.fiap.shared.domain.exception.InvalidBookStateException;
import br.com.fiap.shared.domain.valueObject.Availability;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookTest {

    @Test
    void newBookIsAvailable() {
        assertTrue(Fixtures.book("Dom Casmurro").isAvailable());
    }

    @Test
    void borrowMakesBookBorrowedAndReturnMakesItAvailable() {
        Book book = Fixtures.book("Dom Casmurro");

        book.borrow();
        assertEquals(Availability.BORROWED, book.getAvailability());

        book.returnBook();
        assertEquals(Availability.AVAILABLE, book.getAvailability());
    }

    @Test
    void cannotBorrowBorrowedBook() {
        Book book = Fixtures.book("Dom Casmurro");
        book.borrow();

        assertThrows(BookNotAvailableException.class, book::borrow);
    }

    @Test
    void cannotBorrowOrReserveReservedBook() {
        Book book = Fixtures.book("Dom Casmurro");
        book.reserve();

        assertEquals(Availability.RESERVED, book.getAvailability());
        assertThrows(BookNotAvailableException.class, book::borrow);
        assertThrows(BookNotAvailableException.class, book::reserve);
    }

    @Test
    void reservedBookCanBePickedUp() {
        Book book = Fixtures.book("Dom Casmurro");
        book.reserve();

        book.pickUpReservation();

        assertEquals(Availability.BORROWED, book.getAvailability());
    }

    @Test
    void cannotReturnBookThatIsNotBorrowed() {
        Book book = Fixtures.book("Dom Casmurro");

        assertThrows(InvalidBookStateException.class, book::returnBook);
    }
}
