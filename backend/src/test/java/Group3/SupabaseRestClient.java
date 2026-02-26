package Group3;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class SupabaseRestClient {
    private final RestTemplate rest = new RestTemplate();
    private final String baseUrl;
    private final String apiKey;

    public SupabaseRestClient(String supabaseUrl, String apiKey) {
        this.baseUrl = supabaseUrl.endsWith("/") ? supabaseUrl + "rest/v1" : supabaseUrl + "/rest/v1";
        this.apiKey = apiKey;
    }

    private HttpHeaders headers() {
        HttpHeaders h = new HttpHeaders();
        if (apiKey != null && !apiKey.isEmpty()) {
            h.set("apikey", apiKey);
            h.setBearerAuth(apiKey);
        }
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        h.set("Prefer", "return=representation");
        return h;
    }

    public ResponseEntity<String> get(String pathWithQuery) {
        HttpEntity<Void> entity = new HttpEntity<>(headers());
        return rest.exchange(baseUrl + pathWithQuery, HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity<String> post(String pathWithQuery, Object body) {
        HttpEntity<Object> entity = new HttpEntity<>(body, headers());
        return rest.exchange(baseUrl + pathWithQuery, HttpMethod.POST, entity, String.class);
    }

    public ResponseEntity<String> delete(String pathWithQuery) {
        HttpEntity<Void> entity = new HttpEntity<>(headers());
        return rest.exchange(baseUrl + pathWithQuery, HttpMethod.DELETE, entity, String.class);
    }
}