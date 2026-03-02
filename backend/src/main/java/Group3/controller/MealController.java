package Group3.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Group3.model.Meal;
import Group3.service.MealService;

import java.util.*;

@RestController
@RequestMapping("/meals")
public class MealController {
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    // GET /meals
    @GetMapping
    public List<Meal> getAllMeals() {
        Long usersId = getCurrentUsersId();
        return mealService.getAllMeals(usersId);
    }

    // GET /meals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Meal> getMealById(@PathVariable Long id) {
        Long usersId = getCurrentUsersId();
        return mealService.getMealbyId(id, usersId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // POST /meals
    @PostMapping
    public ResponseEntity<Meal> creatMeal(@RequestBody Meal meal) {
        Long usersId = getCurrentUsersId();
        Meal created = mealService.createMeal(meal, usersId);
        return ResponseEntity.status(201).body(created);
    }

    // Temp until OAuth is integrated:
    private Long getCurrentUsersId() {
        return 1L; // TODO: replace with OAuth principal -> DB user id mapping
    }
}
