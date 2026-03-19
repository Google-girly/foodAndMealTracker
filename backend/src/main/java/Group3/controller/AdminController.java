package Group3.controller;

import Group3.model.Food;
import Group3.model.Meal;
import Group3.model.User;
import Group3.repository.FoodRepository;
import Group3.repository.MealRepository;
import Group3.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final FoodRepository foodRepository;
    private final MealRepository mealRepository;
    /**
     * Constructs an AdminController with the given UserRepository.
     *
     * @param userRepository repository used to access user data
     */


    public AdminController(UserRepository userRepository, FoodRepository foodRepository, MealRepository mealRepository) {
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.mealRepository = mealRepository;
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

    @GetMapping("/meals/{mealId}")
    public ResponseEntity<?> getMealById(
            @PathVariable Long mealId,
            HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return mealRepository.findById(mealId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/meals")
    public ResponseEntity<?> getAllMeals(HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return ResponseEntity.ok(mealRepository.findAll());
    }

    @PostMapping("/meals")
    public ResponseEntity<?> createMeal(
            @RequestBody Meal meal,
            HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        if (meal.getUsersId() == null) {
            return ResponseEntity.badRequest().body("usersId is required");
        }

        if (meal.getName() == null || meal.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Meal name is required");
        }

        if (meal.getMealType() == null || meal.getMealType().isBlank()) {
            return ResponseEntity.badRequest().body("mealType is required");
        }

        try {
            Meal created = mealRepository.save(meal);
            return ResponseEntity.status(201).body(created);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Could not create meal. Check usersId and required fields.");
        }
    }

    @PutMapping("/meals/{mealId}")
    public ResponseEntity<?> updateMeal(
            @PathVariable Long mealId,
            @RequestBody Meal updatedMeal,
            HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return mealRepository.findById(mealId)
                .map(meal -> {
                    meal.setUsersId(updatedMeal.getUsersId());
                    meal.setName(updatedMeal.getName());
                    meal.setMealType(updatedMeal.getMealType());
                    meal.setMealDate(updatedMeal.getMealDate());
                    meal.setDescription(updatedMeal.getDescription());

                    try {
                        return ResponseEntity.ok(mealRepository.save(meal));
                    } catch (DataIntegrityViolationException e) {
                        return ResponseEntity.badRequest().body("Could not update meal. Check usersId and required fields.");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/meals/{mealId}")
    public ResponseEntity<?> patchMeal(
            @PathVariable Long mealId,
            @RequestBody Meal partialMeal,
            HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return mealRepository.findById(mealId)
                .map(meal -> {
                    if (partialMeal.getUsersId() != null) meal.setUsersId(partialMeal.getUsersId());
                    if (partialMeal.getName() != null) meal.setName(partialMeal.getName());
                    if (partialMeal.getMealType() != null) meal.setMealType(partialMeal.getMealType());
                    if (partialMeal.getMealDate() != null) meal.setMealDate(partialMeal.getMealDate());
                    if (partialMeal.getDescription() != null) meal.setDescription(partialMeal.getDescription());

                    try {
                        return ResponseEntity.ok(mealRepository.save(meal));
                    } catch (DataIntegrityViolationException e) {
                        return ResponseEntity.badRequest().body("Could not update meal. Check usersId and required fields.");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/meals/{mealId}")
    public ResponseEntity<?> deleteMeal(
            @PathVariable Long mealId,
            HttpServletRequest request) {

        User admin = getCurrentAdmin(request);
        if (admin == null) {
            return ResponseEntity.status(403).body("Forbidden: Admins only");
        }

        return mealRepository.findById(mealId)
                .map(meal -> {
                    try {
                        mealRepository.delete(meal);
                        mealRepository.flush();
                        return ResponseEntity.noContent().build();
                    } catch (DataIntegrityViolationException e) {
                        return ResponseEntity.status(409).body("Could not delete meal");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
}