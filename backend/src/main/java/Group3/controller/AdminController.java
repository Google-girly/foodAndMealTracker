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

    @GetMapping("/ping")
    public ResponseEntity<String> adminPing(HttpServletRequest request) {

        String userIdStr = (String) request.getAttribute("userId");
        if (userIdStr == null) {
            return ResponseEntity.status(401).body("Unauthorized User");
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(401).body("Unauthorized User");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized User");
        }

        if (!Boolean.TRUE.equals(user.getAdmin())) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return ResponseEntity.ok("Admin access granted for userId=" + userId);
    }
}