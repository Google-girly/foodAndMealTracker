package Group3.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import Group3.model.Food;
import Group3.repository.FoodRepository;

@Service
public class FoodService {

    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    public Optional<Food> getFoodById(Long id) {
        return foodRepository.findById(id);
    }

    public Food createFood(Food food) {
        return foodRepository.save(food);
    }

    public Optional<Food> updateFood(Long id, Food updatedFood) {
        Optional<Food> optional = foodRepository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        Food food = optional.get();
        food.setName(updatedFood.getName());
        food.setCalories(updatedFood.getCalories());
        food.setProtein(updatedFood.getProtein());
        food.setCarbs(updatedFood.getCarbs());
        food.setFat(updatedFood.getFat());
        food.setCreatedById(updatedFood.getCreatedById());
        food.setIsPublic(updatedFood.getIsPublic());
        return Optional.of(foodRepository.save(food));
    }

    public Optional<Food> patchFood(Long id, Food partialFood) {
        Optional<Food> optional = foodRepository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        Food food = optional.get();

        if (partialFood.getName() != null) food.setName(partialFood.getName());
        if (partialFood.getCalories() != null) food.setCalories(partialFood.getCalories());
        if (partialFood.getProtein() != null) food.setProtein(partialFood.getProtein());
        if (partialFood.getCarbs() != null) food.setCarbs(partialFood.getCarbs());
        if (partialFood.getFat() != null) food.setFat(partialFood.getFat());
        if (partialFood.getCreatedById() != null) food.setCreatedById(partialFood.getCreatedById());
        if (partialFood.getIsPublic() != null) food.setIsPublic(partialFood.getIsPublic());

        return Optional.of(foodRepository.save(food));
    }
}
