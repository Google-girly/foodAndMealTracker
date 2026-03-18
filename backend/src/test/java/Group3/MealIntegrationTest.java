package Group3;

import Group3.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import Group3.repository.UserRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MealIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);

        User user = new User();
        user.setEmail("test@example.com"); 
        user.setFullName("Test User");
        user.setAdmin(false);

        user = userRepository.save(user); 
        this.testUserId = user.getId();
    }

    @Test
    void shouldCreateMeal() {
        given()
            .header("X-User-Id", testUserId.toString())
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
            .log().all()
            .statusCode(201)
            .body("name", equalTo("Lunch"));
    }

    // @Test
    // void shouldReturn200WhenNoUserHeader() {
    //     given()
    //     .when()
    //         .get("/meals")
    //     .then()
    //         .log().all()
    //         .statusCode(200);
    // }
}