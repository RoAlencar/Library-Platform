package br.com.fiap.adapter.in.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class UserResourceTest {

    @Test
    void crudUser() {
        String email = UUID.randomUUID() + "@fiap.com.br";
        String id = given().contentType(ContentType.JSON)
                .body(Map.of("name", "Gabriela Alves", "email", email, "type", "LIBRARIAN"))
                .post("/users")
                .then().statusCode(201)
                .body("type", equalTo("LIBRARIAN"))
                .body("active", equalTo(true))
                .extract().path("id");

        given().get("/users/{id}", id).then().statusCode(200).body("email", equalTo(email));

        given().contentType(ContentType.JSON)
                .body(Map.of("name", "Gabriela A. Alves", "email", email, "active", false))
                .put("/users/{id}", id)
                .then().statusCode(200)
                .body("name", equalTo("Gabriela A. Alves"))
                .body("active", equalTo(false));

        given().contentType(ContentType.JSON)
                .body(Map.of("name", "Outra Pessoa", "email", email))
                .post("/users")
                .then().statusCode(409);

        given().delete("/users/{id}", id).then().statusCode(204);
        given().get("/users/{id}", id).then().statusCode(404);
    }

    @Test
    void rejectsInvalidEmail() {
        given().contentType(ContentType.JSON)
                .body(Map.of("name", "Hugo", "email", "not-an-email"))
                .post("/users")
                .then().statusCode(400);
    }
}
