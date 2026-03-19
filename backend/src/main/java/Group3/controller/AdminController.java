package Group3.controller;

import Group3.model.User;
import Group3.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Controller responsible for handling administrative operations.
 * 
 * Provides endpoints for:
 * - Verifying admin access
 * - Retrieving users
 * - Updating user admin status
 * - Deleting users
 */



@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    /**
     * Constructs an AdminController with the given UserRepository.
     *
     * @param userRepository repository used to access user data
     */


    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Verifies whether the current user has admin access.
     *
     * @param request HTTP request containing authentication data
     * @return 200 OK if admin, 401 if unauthorized, 403 if not admin
     */

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


      /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @param request HTTP request containing authentication data
     * @return the user if found, or appropriate error response
     */

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


    /**
     * Retrieves all users in the system.
     *
     * @param request HTTP request containing authentication data
     * @return list of users or forbidden response if not admin
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return ResponseEntity.ok(userRepository.findAll());
    }

    /**
     * Updates the admin status of a user.
     *
     * @param userId the ID of the user to update
     * @param adminStatus the new admin status (true or false)
     * @param request HTTP request containing authentication data
     * @return updated user or error response
     */
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
    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     * @param request HTTP request containing authentication data
     * @return no content if deleted, or error response
     */
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