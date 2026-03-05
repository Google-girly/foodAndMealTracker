package Group3;

import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    /* How to test in PowerShell:
      cd backend
      ./gradlew bootRun to load SpringBoot
      GET http://localhost:8080/health will return
      the Map as a JSON, example:
      "status": "ok",
      "timestamp": "2026-02-24T13:45:12.123Z" */

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "ok",
            "timestamp", Instant.now().toString()
        );
    }
}