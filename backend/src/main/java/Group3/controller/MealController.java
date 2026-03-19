package Group3.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Group3.model.Meal;
import Group3.service.MealService;

import java.util.*;

/**
 * Controller responsible for managing meal-related operations.
 *
 * Provides endpoints to:
 * - Retrieve meals for a user
 * - Retrieve a specific meal
 * - Create, update, and delete meals
 *
 * All endpoints require a valid user ID from the request.
 */
@RestController
@RequestMapping("/meals")
public class MealController {
    private final MealService mealService;
    /**
     * Constructs a MealController with the given MealService.
     *
     * @param mealService service used to manage meal data
     */
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }
    /**
     * Retrieves all meals for the authenticated user.
     *
     * @param request HTTP request containing authentication data
     * @return list of meals or 401 if unauthorized
     */
    // GET /meals
    @GetMapping
    public ResponseEntity<List<Meal>> getAllMeals(HttpServletRequest request) {
        Long usersId = getCurrentUsersId(request);
        if (usersId == null)
            return ResponseEntity.status(401).build();
        return ResponseEntity.ok(mealService.getAllMeals(usersId));
    }
    /**
     * Retrieves a specific meal by ID for the authenticated user.
     *
     * @param id the ID of the meal
     * @param request HTTP request containing authentication data
     * @return meal if found, 401 if unauthorized, or 404 if not found
     */
    // GET /meals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Meal> getMealById(@PathVariable Long id, HttpServletRequest request) {
        Long usersId = getCurrentUsersId(request);
        if (usersId == null) {
            return ResponseEntity.status(401).build();
        }

        return mealService.getMealbyId(id, usersId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    /**
     * Creates a new meal for the authenticated user.
     *
     * @param meal the meal to create
     * @param request HTTP request containing authentication data
     * @return created meal with 201 status or 401 if unauthorized
     */
    // POST /meals
    @PostMapping
    public ResponseEntity<Meal> createMeal(@RequestBody Meal meal, HttpServletRequest request) {
        Long usersId = getCurrentUsersId(request);
        if (usersId == null) {
            return ResponseEntity.status(401).build();
        }

        Meal created = mealService.createMeal(meal, usersId);
        return ResponseEntity.status(201).body(created);
    }

    private Long getCurrentUsersId(HttpServletRequest request) {
        // OAuth middleware sets "userId" on request
        Object attr = request.getAttribute("userId");
        if (attr != null) {
            String authUserId = attr.toString();

            // only works if authUserId is numeric (ex: "1", "2")
            try {
                return Long.parseLong(authUserId);
            } catch (NumberFormatException e) {
                return 1L;
            }
        }

        // fallback to the existing dev/test header
        String header = request.getHeader("X-User-Id");
        if (header == null || header.isBlank())
            return 1L;

        try {
            return Long.parseLong(header);
        } catch (NumberFormatException e) {
            return 1L;
        }
    }

    // Temp until OAuth is integrated:
    /*
     * private Long getCurrentUsersId(HttpServletRequest request) {
     * String header = request.getHeader("X-User-Id");
     * if (header == null || header.isBlank())
     * return 1L;
     * return Long.parseLong(header);
     * }
     */
    
     /**
     * Updates an existing meal completely.
     *
     * @param id the ID of the meal to update
     * @param updatedMeal the updated meal data
     * @param request HTTP request containing authentication data
     * @return updated meal, 401 if unauthorized, or 404 if not found
     */
    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<Meal> updateMeal(@PathVariable Long id,
            @RequestBody Meal updatedMeal,
            HttpServletRequest request) {
        Long usersId = getCurrentUsersId(request);
        if (usersId == null) {
            return ResponseEntity.status(401).build();
        }

        return mealService.updateMeal(id, updatedMeal, usersId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * Partially updates an existing meal.
     *
     * @param id the ID of the meal to update
     * @param partialMeal the partial meal data
     * @param request HTTP request containing authentication data
     * @return updated meal, 401 if unauthorized, or 404 if not found
     */
    // PATCH
    @PatchMapping("/{id}")
    public ResponseEntity<Meal> patchMeal(@PathVariable Long id,
            @RequestBody Meal partialMeal,
            HttpServletRequest request) {
        Long usersId = getCurrentUsersId(request);
        if (usersId == null) {
            return ResponseEntity.status(401).build();
        }

        return mealService.patchMeal(id, partialMeal, usersId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Deletes a meal by its ID.
     *
     * @param id the ID of the meal to delete
     * @return 204 No Content after deletion
     */
    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        mealService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }
}
