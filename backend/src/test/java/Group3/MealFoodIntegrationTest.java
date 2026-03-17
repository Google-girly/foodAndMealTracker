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
public class MealFoodIntegrationTest {
    
    @LocalServerPort
    int port;

    @BeforeEach
    void setup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    //Happy
    @Test
    void shouldCreateMealFood(){
        given()
            .contentType("application/json")
            .body("""
                {
                  "mealId": 1,
                  "foodId": 1,
                  "quantity": 2,
                  "unit": "serving"
                }      
            """)
        .when()
            .post("/meal_foods")
        .then()
            .statusCode(201);
    }
    //Error
    @Test
    void shouldReturn404ForMissingMealFood(){
        when()
            .get("/meal-foods/999")
        .then()
            .statusCode(404);
    }
}
