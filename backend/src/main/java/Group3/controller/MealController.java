package Group3.controller;

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
@RestController
@RequestMapping("/meals")
public class MealController {
    private final MealService mealService;

    public MealController(MealService mealService){
        this.mealService = mealService;
    }

    //GET /meals
    @GetMapping
    public List<Meal> getAllMeals(){
        return mealService.getAllMeals();
    }

    //GET /meals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Meal> getMealById(@PathVariable Long id){
        return mealService.getMealbyId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    //POST /meals
    @PostMapping
    public ResponseEntity<Meal> creatMeal(@RequestBody Meal meal){
        Meal created = mealService.createMeal(meal);
        return ResponseEntity.status(201).body(created);
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

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        mealService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }
}
