package Group3;

import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller providing health check endpoints for the application.
 *
 * Used to verify that the service is running and accessible.
 */
@RestController
public class HealthController {

/**
     * Default constructor for HealthController.
     */
    public HealthController() {
    }

    /**
     * Root endpoint providing basic service information.
     *
     * @return a map containing service status and metadata
     */
    @GetMapping("/")
    public Map<String, Object> root() {
        return Map.of(
            "status", "ok",
            "service", "food-meal-api",
            "health", "/health"
        );
    }

    /* How to test in PowerShell:
      cd backend
      ./gradlew bootRun to load SpringBoot
      GET http://localhost:8080/health will return
      the Map as a JSON, example:
      "status": "ok",
      "timestamp": "2026-02-24T13:45:12.123Z" */

     /**
     * Health check endpoint returning current system status.
     *
     * @return a map containing status and current timestamp
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "ok",
            "timestamp", Instant.now().toString()
        );
    }
}