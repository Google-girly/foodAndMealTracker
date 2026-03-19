package Group3;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

/**
 * Basic application context test.
 *
 * Verifies that the Spring Boot application context loads successfully.
 */
@ActiveProfiles("test")
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FoodAndMealTrackerApplicationTests {

	/**
     * Tests that the application context loads without errors.
     */
	@Test
	void contextLoads() {
	}

}
