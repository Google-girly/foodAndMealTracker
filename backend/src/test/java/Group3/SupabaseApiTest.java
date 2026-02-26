package Group3;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
public class SupabaseApiTest {

    @Value("${supabase.url}")
    String supabaseUrl;

    @Value("${supabase.anonKey}")
    String anonKey;

    @Value("${supabase.serviceRoleKey:}")
    String serviceRoleKey;

    SupabaseRestClient client;

    @BeforeEach
    void setUp() {
        String keyToUse = (serviceRoleKey != null && !serviceRoleKey.isEmpty()) ? serviceRoleKey : anonKey;
        client = new SupabaseRestClient(supabaseUrl, keyToUse);
    }

    @Test
    void canListUsers() {
        ResponseEntity<String> resp = client.get("/users?select=*");
        assertThat(resp.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void canInsertAndDeleteUser_whenServiceRolePresent() {
        Assumptions.assumeTrue(serviceRoleKey != null && !serviceRoleKey.isEmpty(),
                "Service role key not present — skipping write test");

        String email = "test" + UUID.randomUUID() + "@example.com";

        var insertResp = client.post("/users", Map.of(
                "email", email,
                "full_name", "Test User",
                "admin", false
        ));

        assertThat(insertResp.getStatusCode().value()).isIn(200, 201);

        var deleteResp = client.delete("/users?email=eq." + email);
        assertThat(deleteResp.getStatusCode().value()).isIn(200, 204);
    }
}