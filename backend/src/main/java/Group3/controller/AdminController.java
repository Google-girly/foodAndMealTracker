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

    private User getCurrentAdmin(HttpServletRequest request) {

        Long userId = getCurrentUsersId(request);
        if (userId == null) {
        return null;
    }   

    User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
        return null;
        }

        if (!Boolean.TRUE.equals(user.getAdmin())) {
            return null;
        }

    return user;
}

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(
        @PathVariable Long userId,
        HttpServletRequest request) {

    User admin = getCurrentAdmin(request);
    if (admin == null) {
        return ResponseEntity.status(403).body("Forbidden: Admins only");
    }

    return userRepository.findById(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return ResponseEntity.ok(userRepository.findAll());
    }
    @PatchMapping("/users/{userId}")
    public ResponseEntity<?> updateAdminStatus(
            @PathVariable Long userId,
            @RequestParam Boolean adminStatus,
            HttpServletRequest request) {

        User currentAdmin = getCurrentAdmin(request);
        if (currentAdmin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        if (currentAdmin.getId().equals(userId) && !adminStatus) {
            return ResponseEntity.badRequest()
                    .body("You cannot remove your own admin privileges");
        }

        return userRepository.findById(userId)
                .map(user -> {
                    user.setAdmin(adminStatus);
                    userRepository.save(user);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long userId,
            HttpServletRequest request) {

        User currentAdmin = getCurrentAdmin(request);
        if (currentAdmin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

    // Prevent deleting yourself
        if (currentAdmin.getId().equals(userId)) {
             return ResponseEntity.badRequest()
                .body("You cannot delete yourself");
        }

        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}