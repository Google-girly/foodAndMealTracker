package Group3;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for the /health endpoint.
 *
 * Verifies that the health endpoint returns:
 * - HTTP 200 status
 * - Expected JSON fields (status, timestamp)
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthEndpointTest {

    @LocalServerPort
    private int port;

    /**
     * Tests that the /health endpoint returns a valid response.
     *
     * @throws Exception if the HTTP request fails
     */
    @Test
    void health_returns200_andContainsExpectedJsonFields() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + port + "/health"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String body = response.body();
        assertNotNull(body);

        // lightweight checks (no JSON libs required)
        assertTrue(body.contains("\"status\""));
        assertTrue(body.contains("\"ok\""));
        assertTrue(body.contains("\"timestamp\""));
    }
}