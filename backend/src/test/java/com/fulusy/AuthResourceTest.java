package com.fulusy;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class AuthResourceTest {

    @Test
    void register_and_login_happy_path() {
        // Register
        String registerBody = """
                {
                  "email": "test-%d@fulusy.app",
                  "password": "testpass123",
                  "name": "Test User",
                  "startingBalance": 4000,
                  "incomeMode": "fixed",
                  "fixedIncomeAmount": 12000,
                  "fixedIncomeDay": 1,
                  "language": "ar"
                }
                """.formatted(System.currentTimeMillis());

        String token = given()
                .contentType("application/json")
                .body(registerBody)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201)
                .body("token", not(emptyString()))
                .body("name", equalTo("Test User"))
                .extract().path("token");

        // Authenticated /me call
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/me")
                .then()
                .statusCode(200)
                .body("name", equalTo("Test User"))
                .body("currency", equalTo("EGP"))
                .body("language", equalTo("ar"));
    }

    @Test
    void register_duplicate_email_fails() {
        String email = "dup-" + System.currentTimeMillis() + "@fulusy.app";
        String body = """
                {
                  "email": "%s",
                  "password": "testpass123",
                  "name": "Dup User",
                  "startingBalance": 1000,
                  "incomeMode": "variable"
                }
                """.formatted(email);

        given().contentType("application/json").body(body)
                .when().post("/api/auth/register")
                .then().statusCode(201);

        given().contentType("application/json").body(body)
                .when().post("/api/auth/register")
                .then().statusCode(400)
                .body("error", containsString("already registered"));
    }

    @Test
    void me_without_token_is_unauthorized() {
        given().when().get("/api/me").then().statusCode(401);
    }
}
