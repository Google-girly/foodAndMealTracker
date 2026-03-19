package Group3.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Group3.model.Food;
import Group3.service.FoodService;


/**
 * Controller responsible for handling operations related to food items.
 * 
 * Provides endpoints to:
 * - Retrieve all foods
 * - Retrieve a food by ID
 * - Create new food entries
 * - Update or partially update existing foods
 */
@RestController
@RequestMapping("/foods")
public class FoodController {

    private final FoodService foodService;
    /**
     * Constructs a FoodController with the given FoodService.
     *
     * @param foodService service used to manage food data
     */
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }
    /**
     * Retrieves all food items.
     *
     * @return list of all foods
     */
    @GetMapping
    public List<Food> getAllFoods() {
        return foodService.getAllFoods();
    }
    /**
     * Retrieves a specific food item by its ID.
     *
     * @param id the ID of the food item
     * @return the food if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(@PathVariable Long id) {
        return foodService.getFoodById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    /**
     * Creates a new food item.
     *
     * @param food the food object to create
     * @return the created food with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Food> createFood(@RequestBody Food food) {
        Food created = foodService.createFood(food);
        return ResponseEntity.status(201).body(created);
    }
    /**
     * Updates an existing food item completely.
     *
     * @param id the ID of the food to update
     * @param updatedFood the updated food data
     * @return updated food if found, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFood(@PathVariable Long id, @RequestBody Food updatedFood) {
        return foodService.updateFood(id, updatedFood).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    /**
     * Partially updates an existing food item.
     *
     * @param id the ID of the food to update
     * @param partialFood the partial food data to apply
     * @return updated food if found, or 404 if not found
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Food> patchFood(@PathVariable Long id, @RequestBody Food partialFood) {
        return foodService.patchFood(id, partialFood).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
