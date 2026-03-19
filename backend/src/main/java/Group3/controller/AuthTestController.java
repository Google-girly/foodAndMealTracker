package Group3.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Controller used to test authentication and protected routes.
 * 
 * Provides endpoints to verify whether a request contains valid
 * authentication information.
 */
@RestController
@RequestMapping("/api")
public class AuthTestController {

/**
 * Default constructor for AuthTestController.
 */
    public AuthTestController() {
    }
    /**
     * Endpoint to test access to a protected route.
     *
     * Checks if a user ID is present in the request attributes
     * (typically set by authentication middleware).
     *
     * @param request HTTP request containing authentication data
     * @return 200 OK if authenticated, 401 Unauthorized otherwise
     */
    @GetMapping("/protected")
    public ResponseEntity<String> protectedRoute(HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized User");
        }

        return ResponseEntity.ok("Authenticated user: " + userId);
    }
}