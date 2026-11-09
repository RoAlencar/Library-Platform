package br.com.fiap.adapter.in.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ReportResourceTest {

    @Test
    void mostBorrowedBooks() {
        String user = TestData.createUser("Igor Prado");
        String popular = TestData.createBook("Grande Sertão: Veredas", "Guimarães Rosa");
        for (int i = 0; i < 25; i++) {
            given().post("/loans/{id}/return", TestData.borrow(user, popular)).then().statusCode(200);
        }

        var report = given().get("/reports/most-borrowed")
                .then().statusCode(200)
                .body("timeZone", equalTo("America/Sao_Paulo"))
                .body("generatedAt", endsWith("-03:00"))
                .body("items.size()", lessThanOrEqualTo(20))
                .body("items[0].bookId", equalTo(popular))
                .body("items[0].rank", equalTo(1))
                .body("items[0].timesBorrowed", equalTo(25))
                .extract();

        List<Integer> counts = report.path("items.timesBorrowed");
        for (int i = 1; i < counts.size(); i++) {
            assertTrue(counts.get(i - 1) >= counts.get(i));
        }
    }

    @Test
    void activeLoansWithExpectedReturnDate() {
        String bookId = TestData.createBook("A Hora da Estrela", "Clarice Lispector");
        String loanId = TestData.borrow(TestData.createUser("Júlia Ramos"), bookId);

        given().get("/reports/active-loans")
                .then().statusCode(200)
                .body("items.loanId", hasItem(loanId))
                .body("items.find { it.loanId == '%s' }.expectedReturnDate".formatted(loanId), endsWith("-03:00"))
                .body("items.find { it.loanId == '%s' }.overdue".formatted(loanId), equalTo(false));
    }

    @Test
    void openApiIsPublished() {
        given().get("/q/openapi?format=json")
                .then().statusCode(200)
                .body("paths.'/reports/most-borrowed'.get.summary", org.hamcrest.Matchers.notNullValue());
    }
}
