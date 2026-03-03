package Group3.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/meals")
public class MealController {
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    // GET /meals
    @GetMapping
    public List<Meal> getAllMeals(HttpServletRequest request) {
        Long usersId = getCurrentUsersId(request);
        return mealService.getAllMeals(usersId);
    }

    // GET /meals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Meal> getMealById(@PathVariable Long id, HttpServletRequest request) {
        Long usersId = getCurrentUsersId(request);
        return mealService.getMealbyId(id, usersId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // POST /meals
    @PostMapping
    public ResponseEntity<Meal> creatMeal(@RequestBody Meal meal, HttpServletRequest request) {
        Long usersId = getCurrentUsersId(request);
        Meal created = mealService.createMeal(meal, usersId);
        return ResponseEntity.status(201).body(created);
    }

    // Temp until OAuth is integrated:
    private Long getCurrentUsersId(HttpServletRequest request) {
        String header = request.getHeader("X-User-Id");
        if (header == null || header.isBlank())
            return 1L;
        return Long.parseLong(header);
}
    //PUT
    @PutMapping("/{id}")
    public ResponseEntity<Meal> updateMeal(@PathVariable Long id, @RequestBody Meal updatedMeal){
        return mealService.updateMeal(id,updatedMeal).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    //PATCH
    @PatchMapping("/{id}")
    public ResponseEntity<Meal> patchMeal(@PathVariable Long id,@RequestBody Meal partialMeal){
        return mealService.patchMeal(id,partialMeal).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}