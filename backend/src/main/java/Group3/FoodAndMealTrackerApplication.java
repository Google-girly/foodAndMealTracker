package Group3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Food and Meal Tracker application.
 *
 * This class bootstraps the Spring Boot application.
 */
@SpringBootApplication
public class FoodAndMealTrackerApplication {
	/**
     * Default constructor for FoodAndMealTrackerApplication.
     */
	public FoodAndMealTrackerApplication() {
		// No initialization required
	}

	/**
     * Main method used to launch the Spring Boot application.
     *
     * @param args command-line arguments
     */
	public static void main(String[] args) {
		SpringApplication.run(FoodAndMealTrackerApplication.class, args);
	}

}
