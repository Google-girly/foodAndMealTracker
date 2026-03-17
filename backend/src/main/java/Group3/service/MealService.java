package Group3.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import Group3.model.Meal;
import Group3.repository.MealRepository;

@Service
public class MealService {
    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public List<Meal> getAllMeals(Long usersId) {
        return mealRepository.findAllByUsersId(usersId);
    }

    public Optional<Meal> getMealbyId(Long id, Long usersId) {
        return mealRepository.findByIdAndUsersId(id, usersId);
    }

    public Meal createMeal(Meal meal, Long usersId) {
        meal.setUsersId(usersId);
        return mealRepository.save(meal);
    }

    public Optional<Meal> updateMeal(Long id, Meal updatedMeal, Long usersId) {
        // only update if the meal belongs to the current user
        Optional<Meal> optionalMeal = mealRepository.findByIdAndUsersId(id, usersId);

        if (optionalMeal.isEmpty()) {
            return Optional.empty();
        }

        Meal meal = optionalMeal.get();

        // Never allow changing ownership
        meal.setUsersId(usersId);

        meal.setName(updatedMeal.getName());
        meal.setMealType(updatedMeal.getMealType());
        meal.setMealDate(updatedMeal.getMealDate());
        meal.setDescription(updatedMeal.getDescription());
        meal.setUpdatedAt(updatedMeal.getUpdatedAt());
        Meal saved = mealRepository.save(meal);
        return Optional.of(saved);
    }

    public Optional<Meal> patchMeal(Long id, Meal partialMeal, Long usersId) {
        // Only patch if the meal belongs to the current user
        Optional<Meal> optionalMeal = mealRepository.findByIdAndUsersId(id, usersId);

        if (optionalMeal.isEmpty()) {
            return Optional.empty();
        }

        Meal meal = optionalMeal.get();

        // Never allow changing ownership
        meal.setUsersId(usersId);

        if (partialMeal.getName() != null) {
            meal.setName(partialMeal.getName());
        }
        if (partialMeal.getMealType() != null) {
            meal.setMealType(partialMeal.getMealType());
        }
        if (partialMeal.getMealDate() != null) {
            meal.setMealDate(partialMeal.getMealDate());
        }
        if (partialMeal.getDescription() != null) {
            meal.setDescription(partialMeal.getDescription());
        }
        Meal saved = mealRepository.save(meal);
        return Optional.of(saved);
    }

    public void deleteMeal(Long id) {
        mealRepository.deleteById(id);
    }
}
