package Group3.service;
import Group3.model.Meal;
import Group3.service.MealService;

import org.springframework.stereotype.*;

import java.util.*;

import Group3.repository.MealRepository;
@Service
public class MealService {
    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository){
        this.mealRepository = mealRepository;
    }

    public List<Meal> getAllMeals(){
        return mealRepository.findAll();
    }

    public Optional<Meal> getMealbyId(Long id){
        return mealRepository.findById(id);
    }
    public Meal createMeal(Meal meal){
        return mealRepository.save(meal);
    }
}
