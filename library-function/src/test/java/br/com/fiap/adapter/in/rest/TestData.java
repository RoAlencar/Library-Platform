package br.com.fiap.adapter.in.rest;

import io.restassured.http.ContentType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;

final class TestData {

    private TestData() {
    }

    static String randomIsbn() {
        return "978" + ThreadLocalRandom.current().nextLong(1_000_000_000L, 9_999_999_999L);
    }

    static String createBook(String name, String author) {
        return createBook(name, author, randomIsbn());
    }

    static String createBook(String name, String author, String isbn) {
        return given().contentType(ContentType.JSON)
                .body(Map.of("name", name, "author", author, "description", "Test book", "isbn", isbn))
                .post("/books")
                .then().statusCode(201)
                .extract().path("id");
    }

    static String createUser(String name) {
        return given().contentType(ContentType.JSON)
                .body(Map.of("name", name, "email", UUID.randomUUID() + "@fiap.com.br"))
                .post("/users")
                .then().statusCode(201)
                .extract().path("id");
    }

    static String borrow(String userId, String bookId) {
        return given().contentType(ContentType.JSON)
                .body(Map.of("userId", userId, "bookId", bookId))
                .post("/loans")
                .then().statusCode(201)
                .extract().path("id");
    }
}
