package Group3.controller;

import Group3.model.Food;
import Group3.model.User;
import Group3.repository.FoodRepository;
import Group3.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    public AdminController(UserRepository userRepository, FoodRepository foodRepository) {
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
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

    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @RequestBody User user,
            HttpServletRequest request) {

        User currentAdmin = getCurrentAdmin(request);
        if (currentAdmin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        try {
            if (user.getAdmin() == null) {
                user.setAdmin(false);
            }
            User created = userRepository.save(user);
            return ResponseEntity.status(201).body(created);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Could not create user. Email may already exist.");
        }
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody User updatedUser,
            HttpServletRequest request) {

        User currentAdmin = getCurrentAdmin(request);
        if (currentAdmin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return userRepository.findById(userId)
                .map(user -> {
                    if (currentAdmin.getId().equals(userId) && !Boolean.TRUE.equals(updatedUser.getAdmin())) {
                        return ResponseEntity.badRequest().body("You cannot remove your own admin privileges");
                    }

                    user.setEmail(updatedUser.getEmail());
                    user.setFullName(updatedUser.getFullName());
                    user.setAdmin(updatedUser.getAdmin());

                    try {
                        return ResponseEntity.ok(userRepository.save(user));
                    } catch (DataIntegrityViolationException e) {
                        return ResponseEntity.badRequest().body("Could not update user. Email may already exist.");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
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

    @PatchMapping("/users/{userId}/details")
    public ResponseEntity<?> patchUserDetails(
            @PathVariable Long userId,
            @RequestBody User partialUser,
            HttpServletRequest request) {

        User currentAdmin = getCurrentAdmin(request);
        if (currentAdmin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return userRepository.findById(userId)
                .map(user -> {
                    Boolean requestedAdmin = partialUser.getAdmin();
                    if (currentAdmin.getId().equals(userId) && Boolean.FALSE.equals(requestedAdmin)) {
                        return ResponseEntity.badRequest().body("You cannot remove your own admin privileges");
                    }

                    if (partialUser.getEmail() != null) {
                        user.setEmail(partialUser.getEmail());
                    }
                    if (partialUser.getFullName() != null) {
                        user.setFullName(partialUser.getFullName());
                    }
                    if (requestedAdmin != null) {
                        user.setAdmin(requestedAdmin);
                    }

                    try {
                        return ResponseEntity.ok(userRepository.save(user));
                    } catch (DataIntegrityViolationException e) {
                        return ResponseEntity.badRequest().body("Could not update user. Email may already exist.");
                    }
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
                    try {
                        userRepository.delete(user);
                        userRepository.flush();
                        return ResponseEntity.noContent().build();
                    } catch (DataIntegrityViolationException e) {
                        return ResponseEntity.status(409)
                                .body("Could not delete user because related records still exist");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/foods/{foodId}")
    public ResponseEntity<?> getFoodById(
            @PathVariable Long foodId,
            HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return foodRepository.findById(foodId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/foods")
    public ResponseEntity<?> getAllFoods(HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return ResponseEntity.ok(foodRepository.findAll());
    }

    @PostMapping("/foods")
    public ResponseEntity<?> createFood(
            @RequestBody Food food,
            HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        if (food.getName() == null || food.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Food name is required");
        }

        if (food.getIsPublic() == null) {
            food.setIsPublic(false);
        }
        if (food.getCreatedById() == null) {
            food.setCreatedById(admin.getId());
        }

        try {
            Food created = foodRepository.save(food);
            return ResponseEntity.status(201).body(created);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Could not create food");
        }
    }

    @PutMapping("/foods/{foodId}")
    public ResponseEntity<?> updateFood(
            @PathVariable Long foodId,
            @RequestBody Food updatedFood,
            HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return foodRepository.findById(foodId)
                .map(food -> {
                    food.setName(updatedFood.getName());
                    food.setCalories(updatedFood.getCalories());
                    food.setProtein(updatedFood.getProtein());
                    food.setCarbs(updatedFood.getCarbs());
                    food.setFat(updatedFood.getFat());
                    food.setCreatedById(updatedFood.getCreatedById() != null ? updatedFood.getCreatedById() : admin.getId());
                    food.setIsPublic(updatedFood.getIsPublic());

                    try {
                        return ResponseEntity.ok(foodRepository.save(food));
                    } catch (DataIntegrityViolationException e) {
                        return ResponseEntity.badRequest().body("Could not update food");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/foods/{foodId}")
    public ResponseEntity<?> patchFood(
            @PathVariable Long foodId,
            @RequestBody Food partialFood,
            HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return foodRepository.findById(foodId)
                .map(food -> {
                    if (partialFood.getName() != null) food.setName(partialFood.getName());
                    if (partialFood.getCalories() != null) food.setCalories(partialFood.getCalories());
                    if (partialFood.getProtein() != null) food.setProtein(partialFood.getProtein());
                    if (partialFood.getCarbs() != null) food.setCarbs(partialFood.getCarbs());
                    if (partialFood.getFat() != null) food.setFat(partialFood.getFat());
                    if (partialFood.getCreatedById() != null) food.setCreatedById(partialFood.getCreatedById());
                    if (partialFood.getIsPublic() != null) food.setIsPublic(partialFood.getIsPublic());

                    try {
                        return ResponseEntity.ok(foodRepository.save(food));
                    } catch (DataIntegrityViolationException e) {
                        return ResponseEntity.badRequest().body("Could not update food");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/foods/{foodId}")
    public ResponseEntity<?> deleteFood(
            @PathVariable Long foodId,
            HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return foodRepository.findById(foodId)
                .map(food -> {
                    try {
                        foodRepository.delete(food);
                        foodRepository.flush();
                        return ResponseEntity.noContent().build();
                    } catch (DataIntegrityViolationException e) {
                        return ResponseEntity.status(409)
                                .body("Could not delete food because it is used by an existing meal");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
}