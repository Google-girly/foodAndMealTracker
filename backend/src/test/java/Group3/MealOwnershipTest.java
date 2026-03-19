package Group3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.context.ActiveProfiles;
import Group3.model.User;
import Group3.repository.UserRepository;

/**
 * Integration tests for meal ownership and access control.
 *
 * Verifies that users cannot access meals that belong to other users.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MealOwnershipTest {

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    /**
     * Tests that a user cannot access another user's meal.
     *
     * @throws Exception if HTTP requests fail
     */
    @Test
    void userCannotAccessAnotherUsersMeal_returns404() throws Exception {
        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setFullName("User One");
        user1.setAdmin(false);
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setFullName("User Two");
        user2.setAdmin(false);
        user2 = userRepository.save(user2);

        HttpClient client = HttpClient.newHttpClient();

        // 1) Create a meal as user 1
        String createJson = """
                {
                  "name": "Ownership Test Meal",
                  "mealType": "LUNCH",
                  "mealDate": "2026-03-02",
                  "description": "created in test"
                }
                """;

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + port + "/meals"))
                .header("Content-Type", "application/json")
                .header("X-User-Id", user1.getId().toString())
                .POST(HttpRequest.BodyPublishers.ofString(createJson))
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, createResponse.statusCode(), "POST /meals should return 201");

        String createdBody = createResponse.body();
        assertNotNull(createdBody);

        // lightweight ID extraction: find `"id":<number>`
        Long createdId = extractId(createdBody);
        assertNotNull(createdId, "Created meal should contain an id");

        // 2) Attempt to fetch as user 2 -> should be 404
        HttpRequest getAsUser2 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + port + "/meals/" + createdId))
                .header("X-User-Id", user2.getId().toString())
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getAsUser2, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, getResponse.statusCode(), "User 2 should not access user 1 meal");
    }

     /**
     * Extracts the "id" field from a JSON string.
     *
     * @param json the JSON response string
     * @return extracted ID or null if not found
     */
    // Very small helper to avoid bringing in JSON libs:
    private Long extractId(String json) {
        // expects something like ..."id":123,...
        int idx = json.indexOf("\"id\":");
        if (idx == -1)
            return null;
        int start = idx + 5;

        // skip spaces
        while (start < json.length() && Character.isWhitespace(json.charAt(start)))
            start++;

        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end)))
            end++;

        if (start == end)
            return null;
        return Long.parseLong(json.substring(start, end));
    }
}