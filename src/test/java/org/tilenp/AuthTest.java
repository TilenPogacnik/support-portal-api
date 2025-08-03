package org.tilenp;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AuthTest {
    
    @Test
    public void shouldReturn401WhenAccessingConversationsWithoutAuth() {
        given()
            .when().get("/conversations")
            .then()
            .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }
    
    @Test
    public void shouldReturn401WhenAccessingConversationWithoutAuth() {
        given()
            .when().get("/conversations/1")
            .then()
            .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

}