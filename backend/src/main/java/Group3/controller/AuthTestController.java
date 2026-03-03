package Group3.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthTestController {

    @GetMapping("/protected")
    public ResponseEntity<String> protectedRoute(HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized User");
        }

        return ResponseEntity.ok("Authenticated user: " + userId);
    }
}