package Group3.controller;

import java.util.List;

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

import Group3.model.MealFood;
import Group3.service.MealFoodService;

/**
 * Controller responsible for managing relationships between meals and foods.
 *
 * Provides endpoints to:
 * - Retrieve all meal-food relationships
 * - Retrieve a specific meal-food entry
 * - Create, update, and delete meal-food entries
 */
@RestController
@RequestMapping("/meal-foods")
public class MealFoodController {

    private final MealFoodService mealFoodService;
    /**
     * Constructs a MealFoodController with the given MealFoodService.
     *
     * @param mealFoodService service used to manage meal-food data
     */
    public MealFoodController(MealFoodService mealFoodService) {
        this.mealFoodService = mealFoodService;
    }
        /**
        * Retrieves all meal-food relationships.
        *
        * @return list of all meal-food entries
        */
    @GetMapping
    public List<MealFood> getAllMealFoods() {
        return mealFoodService.getAllMealFoods();
    }
    /**
     * Retrieves a specific meal-food entry by its ID.
     *
     * @param id the ID of the meal-food entry
     * @return the meal-food entry if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<MealFood> getMealFoodById(@PathVariable Long id) {
        return mealFoodService.getMealFoodById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    /**
     * Creates a new meal-food relationship.
     *
     * @param mealFood the meal-food entry to create
     * @return created meal-food entry with 201 status
     */
    @PostMapping
    public ResponseEntity<MealFood> createMealFood(@RequestBody MealFood mealFood) {
        MealFood created = mealFoodService.createMealFood(mealFood);
        return ResponseEntity.status(201).body(created);
    }
    /**
     * Updates an existing meal-food entry completely.
     *
     * @param id the ID of the meal-food entry to update
     * @param updated the updated meal-food data
     * @return updated meal-food entry if found, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<MealFood> updateMealFood(@PathVariable Long id, @RequestBody MealFood updated) {
        return mealFoodService.updateMealFood(id, updated).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
        /**
        * Partially updates an existing meal-food entry.
        *
        * @param id the ID of the meal-food entry to update
        * @param partial the partial meal-food data to apply
        * @return updated meal-food entry if found, or 404 if not found
        */
    @PatchMapping("/{id}")
    public ResponseEntity<MealFood> patchMealFood(@PathVariable Long id, @RequestBody MealFood partial) {
        return mealFoodService.patchMealFood(id, partial).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    /**
     * Deletes a meal-food entry by its ID.
     *
     * @param id the ID of the meal-food entry to delete
     * @return 204 No Content after deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealFood(@PathVariable Long id) {
        mealFoodService.deleteMealFood(id);
        return ResponseEntity.noContent().build();
    }
}
