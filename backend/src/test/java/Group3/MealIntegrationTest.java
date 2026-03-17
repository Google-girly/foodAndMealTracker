package Group3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.test.context.ActiveProfiles;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")

public class MealIntegrationTest {
    @LocalServerPort
    int port;
    int userId;

    @BeforeEach
    void setup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        userId = 
            given()
                .contentType("application/json")
                .body("""
                        {
                         "email": "seed@test.com",
                        "fullName": "Seed User",
                        "admin": true
                        }
                        """)
                .when()
                    .post("/users")
                .then()
                    .statusCode(201)
                    .extract()
                    .path("id");
}
    //happy path
    @Test
    void shouldCreateMeal(){
        given()
            .header("X-User-Id", "1")
            .contentType("application/json")
            .body("""
                {
                  "name": "Lunch",
                  "mealType": "LUNCH",
                  "mealDate": "2026-03-01",
                  "description": "Chicken"
                }
            """)
        .when()
            .post("/meals")
        .then()
            .statusCode(201)
            .body("name", equalTo("Lunch"));
    }

    //error
    @Test
    void shouldReturn200WhenNoUserHeader(){
        given()
        .when()
            .get("/meals")
        .then()
            .log().all()
            .statusCode(200);
    }
}