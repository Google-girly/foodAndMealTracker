package Group3.service;

import Group3.model.Meal;
//commented out b/c it's the class importing itself
//import Group3.service.MealService;
import Group3.repository.MealRepository;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class MealService {
    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository){
        this.mealRepository = mealRepository;
    }

    public List<Meal> getAllMeals(Long usersId){
        return mealRepository.findAllByUsersId(usersId);
    }

    public Optional<Meal> getMealbyId(Long id, Long usersId){
        return mealRepository.findByIdAndUsersId(id, usersId);
    }

    public Meal createMeal(Meal meal, Long usersId){
        meal.setUsersId(usersId);
        return mealRepository.save(meal);
    }
}