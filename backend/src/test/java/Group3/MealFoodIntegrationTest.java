package Group3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MealFoodIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

@Test
void shouldCreateMealFood() {

    
    Integer userId =
        given()
            .contentType("application/json")
            .body("""
                {
                  "email": "test@test.com",
                  "fullName": "Test User",
                  "admin": true
                }
            """)
        .when()
            .post("/users")
        .then()
            .extract()
            .path("id");

    
    Integer mealId =
        given()
            .header("X-User-Id", userId.toString())
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
            .extract()
            .path("id");

    
    Integer foodId =
        given()
            .contentType("application/json")
            .body("""
                {
                  "name": "Chicken",
                  "calories": 200
                }
            """)
        .when()
            .post("/foods")
        .then()
            .extract()
            .path("id");

    
    given()
        .contentType("application/json")
        .body("""
            {
              "mealId": %d,
              "foodId": %d,
              "quantity": 2,
              "unit": "serving"
            }
        """.formatted(mealId, foodId))
    .when()
        .post("/meal-foods")
    .then()
        .log().all()
        .statusCode(201);
}

    @Test
    void shouldReturn404ForMissingMealFood() {
        given()
        .when()
            .get("/meal_foods/999")
        .then()
            .log().all()
            .statusCode(404);
    }
}