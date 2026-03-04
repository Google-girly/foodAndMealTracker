package Group3.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import Group3.model.MealFood;
import Group3.repository.MealFoodRepository;

@Service
public class MealFoodService {

    private final MealFoodRepository mealFoodRepository;

    public MealFoodService(MealFoodRepository mealFoodRepository) {
        this.mealFoodRepository = mealFoodRepository;
    }

    public List<MealFood> getAllMealFoods() {
        return mealFoodRepository.findAll();
    }

    public Optional<MealFood> getMealFoodById(Long id) {
        return mealFoodRepository.findById(id);
    }

    public MealFood createMealFood(MealFood mealFood) {
        return mealFoodRepository.save(mealFood);
    }

    public Optional<MealFood> updateMealFood(Long id, MealFood updated) {
        Optional<MealFood> optional = mealFoodRepository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        MealFood mf = optional.get();
        mf.setMealId(updated.getMealId());
        mf.setFoodId(updated.getFoodId());
        mf.setQuantity(updated.getQuantity());
        mf.setUnit(updated.getUnit());
        return Optional.of(mealFoodRepository.save(mf));
    }

    public Optional<MealFood> patchMealFood(Long id, MealFood partial) {
        Optional<MealFood> optional = mealFoodRepository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        MealFood mf = optional.get();
        if (partial.getMealId() != null) mf.setMealId(partial.getMealId());
        if (partial.getFoodId() != null) mf.setFoodId(partial.getFoodId());
        if (partial.getQuantity() != null) mf.setQuantity(partial.getQuantity());
        if (partial.getUnit() != null) mf.setUnit(partial.getUnit());

        return Optional.of(mealFoodRepository.save(mf));
    }

    public void deleteMealFood(Long id) {
        mealFoodRepository.deleteById(id);
    }
}
