package org.tilenp;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tilenp.dto.CreateConversationDTO;
import org.tilenp.entities.User;
import org.tilenp.enums.ConversationTopic;
import org.tilenp.enums.UserRole;

import jakarta.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.doReturn;

@QuarkusTest
public class CreateConversationTest {

    @InjectSpy
    CurrentUser currentUser;

    private User testUser;
    private CreateConversationDTO validRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.id = 1L;
        testUser.username = "testuser";
        testUser.name = "Test User";
        testUser.userRole = UserRole.USER.toString();

        // Setup valid request
        validRequest = new CreateConversationDTO(
            ConversationTopic.TECHNICAL,
            "I need help with my account"
        );
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"USER"})
    void createConversation_ValidRequest_ReturnsCreatedConversation() {
        // Arrange
        doReturn(testUser).when(currentUser).get();

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(validRequest)
            .when()
            .post("/conversations")
            .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .body("customer.username", equalTo("testuser"))
            .body("operator", nullValue())
            .body("topic", equalTo("TECHNICAL"))
            .body("status", equalTo("WAITING"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"USER"})
    void createConversation_MissingTopic_ReturnsBadRequest() {
        // Arrange
        doReturn(testUser).when(currentUser).get();
        CreateConversationDTO invalidRequest = new CreateConversationDTO(null, "Test message");

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
            .when()
            .post("/conversations")
            .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .body("error", equalTo("Topic is required!"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"USER"})
    void createConversation_EmptyMessage_ReturnsBadRequest() {
        // Arrange
        doReturn(testUser).when(currentUser).get();
        CreateConversationDTO invalidRequest = new CreateConversationDTO(ConversationTopic.SERVICES, " ");

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
            .when()
            .post("/conversations")
            .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .body("error", equalTo("Initial message is required!"));
    }

    @Test
    @TestSecurity(user = "operator", roles = {"OPERATOR"})
    void createConversation_OperatorRole_ReturnsForbidden() {
        // Arrange
        User operator = new User();
        operator.id = 2L;
        operator.username = "operator";
        operator.userRole = UserRole.OPERATOR.toString();
        doReturn(operator).when(currentUser).get();

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(validRequest)
            .when()
            .post("/conversations")
            .then()
            .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"USER"})
    void createConversation_Unauthenticated_ReturnsUnauthorized() {
        // Arrange - No authentication provided
        given()
            .contentType(ContentType.JSON)
            .body(validRequest)
            .when()
            .post("/conversations")
            .then()
            .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }
}