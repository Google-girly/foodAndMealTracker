package Group3.controller;

import Group3.model.User;
import Group3.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET /admin/ping
    @GetMapping("/ping")
    public ResponseEntity<String> adminPing(HttpServletRequest request) {

        Long usersId = getCurrentUsersId(request);
        if (usersId == null) {
            return ResponseEntity.status(401).body("Unauthorized User");
        }

        User user = userRepository.findById(usersId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized User");
        }

        if (!Boolean.TRUE.equals(user.getAdmin())) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return ResponseEntity.ok("Admin access granted");
    }

    private Long getCurrentUsersId(HttpServletRequest request) {
        // OAuth middleware sets "userId" on request
        Object attr = request.getAttribute("userId");
        if (attr != null) {
            try {
                return Long.parseLong(attr.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // fallback to the existing dev/test header
        String header = request.getHeader("X-User-Id");
        if (header == null || header.isBlank()) return null;

        try {
            return Long.parseLong(header);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}