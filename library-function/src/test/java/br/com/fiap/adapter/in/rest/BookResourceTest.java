package br.com.fiap.adapter.in.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class BookResourceTest {

    @Test
    void crudBook() {
        String isbn = TestData.randomIsbn();
        var created = given().contentType(ContentType.JSON)
                .body(Map.of("name", "Memórias Póstumas", "author", "Machado de Assis", "isbn", isbn))
                .when().post("/books")
                .then().statusCode(201)
                .body("name", equalTo("Memórias Póstumas"))
                .body("availability", equalTo("AVAILABLE"))
                .extract();
        String id = created.path("id");
        assertTrue(created.header("Location").endsWith("/books/" + id));

        given().queryParam("isbn", isbn).get("/books")
                .then().statusCode(200)
                .body("content[0].id", equalTo(id));

        given().get("/books/{id}", id)
                .then().statusCode(200)
                .body("isbn", equalTo(isbn));

        given().contentType(ContentType.JSON)
                .body(Map.of("name", "Memórias Póstumas de Brás Cubas", "author", "Machado de Assis",
                        "description", "Romance", "isbn", isbn))
                .when().put("/books/{id}", id)
                .then().statusCode(200)
                .body("name", equalTo("Memórias Póstumas de Brás Cubas"))
                .body("description", equalTo("Romance"));

        given().delete("/books/{id}", id).then().statusCode(204);
        given().get("/books/{id}", id).then().statusCode(404);
    }

    @Test
    void filtersAndPaginates() {
        String author = "Autor " + UUID.randomUUID();
        for (int i = 1; i <= 3; i++) {
            TestData.createBook("Coleção Volume " + i, author);
        }

        given().queryParam("author", author.toUpperCase()).queryParam("page", 0).queryParam("size", 2)
                .get("/books")
                .then().statusCode(200)
                .body("content", hasSize(2))
                .body("totalElements", equalTo(3))
                .body("totalPages", equalTo(2))
                .body("content.author", everyItem(equalTo(author)));

        given().queryParam("author", author).queryParam("page", 1).queryParam("size", 2)
                .get("/books")
                .then().statusCode(200)
                .body("content", hasSize(1));

        given().queryParam("name", "volume 2").queryParam("author", author)
                .get("/books")
                .then().statusCode(200)
                .body("content", hasSize(1))
                .body("content[0].name", containsStringIgnoringCase("volume 2"));

        given().get("/books")
                .then().statusCode(200)
                .body("totalElements", greaterThanOrEqualTo(3));
    }

    @Test
    void rejectsInvalidAndDuplicateBooks() {
        given().contentType(ContentType.JSON)
                .body(Map.of("name", "", "author", "X", "isbn", "123"))
                .post("/books")
                .then().statusCode(400);

        String isbn = TestData.randomIsbn();
        TestData.createBook("Original", "Autor", isbn);
        given().contentType(ContentType.JSON)
                .body(Map.of("name", "Copy", "author", "Autor", "isbn", isbn))
                .post("/books")
                .then().statusCode(409);
    }

    @Test
    void unknownBookReturns404() {
        given().get("/books/{id}", UUID.randomUUID())
                .then().statusCode(404)
                .body("status", equalTo(404));
    }
}
