package br.com.fiap.adapter.in.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class LoanResourceTest {

    @Test
    void borrowAndReturnFlow() {
        String userId = TestData.createUser("Ana Souza");
        String bookId = TestData.createBook("O Cortiço", "Aluísio Azevedo");

        var loan = given().contentType(ContentType.JSON)
                .body(Map.of("userId", userId, "bookId", bookId))
                .when().post("/loans")
                .then().statusCode(201)
                .body("status", equalTo("ACTIVE"))
                .body("loanDate", endsWith("-03:00"))
                .body("returnDate", nullValue())
                .extract();
        String loanId = loan.path("id");
        Duration period = Duration.between(OffsetDateTime.parse(loan.path("loanDate")),
                OffsetDateTime.parse(loan.path("dueDate")));
        assertEquals(Duration.ofDays(14), period);

        given().get("/books/{id}", bookId).then().body("availability", equalTo("BORROWED"));
        given().get("/loans/active?size=100").then().statusCode(200).body("content.id", hasItem(loanId));

        String otherUser = TestData.createUser("Bruno Lima");
        given().contentType(ContentType.JSON)
                .body(Map.of("userId", otherUser, "bookId", bookId))
                .post("/loans")
                .then().statusCode(409);

        given().post("/loans/{id}/return", loanId)
                .then().statusCode(200)
                .body("status", equalTo("RETURNED"))
                .body("returnDate", notNullValue());

        given().get("/books/{id}", bookId).then().body("availability", equalTo("AVAILABLE"));
        given().get("/loans/active?size=100").then().body("content.id", not(hasItem(loanId)));
        given().post("/loans/{id}/return", loanId).then().statusCode(409);

        given().get("/users/{id}/loans", userId)
                .then().statusCode(200)
                .body("totalElements", equalTo(1))
                .body("content[0].id", equalTo(loanId))
                .body("content[0].status", equalTo("RETURNED"));
    }

    @Test
    void reservationIsHeldForUserAfterReturn() {
        String borrower = TestData.createUser("Carla Dias");
        String reserver = TestData.createUser("Diego Rocha");
        String bookId = TestData.createBook("Vidas Secas", "Graciliano Ramos");
        String loanId = TestData.borrow(borrower, bookId);

        String reservationId = given().contentType(ContentType.JSON)
                .body(Map.of("userId", reserver))
                .post("/loans/{id}/reserve", loanId)
                .then().statusCode(201)
                .body("status", equalTo("RESERVED"))
                .extract().path("id");

        given().post("/loans/{id}/return", loanId).then().statusCode(200);
        given().get("/books/{id}", bookId).then().body("availability", equalTo("RESERVED"));

        given().contentType(ContentType.JSON)
                .body(Map.of("userId", borrower, "bookId", bookId))
                .post("/loans")
                .then().statusCode(409);

        given().contentType(ContentType.JSON)
                .body(Map.of("userId", reserver, "bookId", bookId))
                .post("/loans")
                .then().statusCode(201)
                .body("id", equalTo(reservationId))
                .body("status", equalTo("ACTIVE"));
    }

    @Test
    void unknownResourcesReturn404() {
        String userId = TestData.createUser("Elisa Melo");
        given().contentType(ContentType.JSON)
                .body(Map.of("userId", userId, "bookId", "00000000-0000-0000-0000-000000000000"))
                .post("/loans")
                .then().statusCode(404);
        given().post("/loans/{id}/return", "00000000-0000-0000-0000-000000000000").then().statusCode(404);
    }

    @Test
    void borrowedBookCannotBeDeleted() {
        String bookId = TestData.createBook("Iracema", "José de Alencar");
        TestData.borrow(TestData.createUser("Fábio Nunes"), bookId);

        given().delete("/books/{id}", bookId).then().statusCode(409);
    }
}
