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
    public Optional<Meal> updateMeal(Long id, Meal updatedMeal){
        Optional<Meal> optionalMeal = mealRepository.findById(id);
        
        if(optionalMeal.isEmpty()){
            return Optional.empty();
        }

        Meal meal = optionalMeal.get();

        meal.setUsersId(updatedMeal.getUsersId());
        meal.setName(updatedMeal.getName());
        meal.setMealType(updatedMeal.getMealType());
        meal.setMealDate(updatedMeal.getMealDate());
        meal.setDescription(updatedMeal.getDescription());
        meal.setUpdatedAt(updatedMeal.getUpdatedAt());
        Meal saved = mealRepository.save(meal);
        return Optional.of(saved);
    }

    public Optional<Meal> patchMeal(Long id, Meal partialMeal){
        Optional<Meal> optionalMeal = mealRepository.findById(id);

        if(optionalMeal.isEmpty()){
            return Optional.empty();
        }

        Meal meal = optionalMeal.get();
        
        if(partialMeal.getUsersId() != null){
            meal.setUsersId(partialMeal.getUsersId());
        }
        if(partialMeal.getName()!=null){
            meal.setName(partialMeal.getName());
        }
        if(partialMeal.getMealType()!=null){
            meal.setMealType(partialMeal.getMealType());
        }
        if(partialMeal.getMealDate() != null){
            meal.setMealDate(partialMeal.getMealDate());
        }
        if(partialMeal.getDescription() != null){
            meal.setDescription(partialMeal.getDescription());
        }
        Meal saved = mealRepository.save(meal);
        return Optional.of(saved);
    }
}
