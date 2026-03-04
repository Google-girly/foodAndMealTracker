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

@RestController
@RequestMapping("/meal-foods")
public class MealFoodController {

    private final MealFoodService mealFoodService;

    public MealFoodController(MealFoodService mealFoodService) {
        this.mealFoodService = mealFoodService;
    }

    @GetMapping
    public List<MealFood> getAllMealFoods() {
        return mealFoodService.getAllMealFoods();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealFood> getMealFoodById(@PathVariable Long id) {
        return mealFoodService.getMealFoodById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MealFood> createMealFood(@RequestBody MealFood mealFood) {
        MealFood created = mealFoodService.createMealFood(mealFood);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealFood> updateMealFood(@PathVariable Long id, @RequestBody MealFood updated) {
        return mealFoodService.updateMealFood(id, updated).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MealFood> patchMealFood(@PathVariable Long id, @RequestBody MealFood partial) {
        return mealFoodService.patchMealFood(id, partial).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealFood(@PathVariable Long id) {
        mealFoodService.deleteMealFood(id);
        return ResponseEntity.noContent().build();
    }
}
