package Group3.service;

import Group3.model.Meal;
import Group3.repository.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.time.LocalDate;

public class MealServiceTest {
    private MealRepository mealRepository;
    private MealService mealService;

    @BeforeEach
    void setUp(){
        mealRepository = Mockito.mock(MealRepository.class);
        mealService = new MealService(mealRepository);
    }
    @Test
    void testUpdateMeal_put_Success(){
        Meal existingMeal = new Meal();
        existingMeal.setName("Old Name");
        existingMeal.setMealType("BREAKFAST");
        existingMeal.setMealDate(LocalDate.of(2026, 2, 26));
        existingMeal.setDescription("Old Description");

        Meal updatedMeal = new Meal();
        updatedMeal.setName("New Name");
        updatedMeal.setMealType("DINNER");
        updatedMeal.setMealDate(LocalDate.of(2026, 3, 1));
        updatedMeal.setDescription("New Description");

        when(mealRepository.findById(1L)).thenReturn(Optional.of(existingMeal));
        when(mealRepository.save(any(Meal.class))).thenReturn(existingMeal);

        Optional<Meal> result = mealService.updateMeal(1L,updatedMeal);

        assertTrue(result.isPresent());
        assertEquals("New Name",result.get().getName());
        assertEquals("DINNER", result.get().getMealType());
        assertEquals("New Description", result.get().getDescription());
    }

    @Test
    void testPatchMeal_Success(){
        Meal existingMeal = new Meal();
        existingMeal.setName("Original name");
        existingMeal.setMealType("BREAKFAST");

        Meal patchMeal = new Meal();
        patchMeal.setName("Updated Name");

        when(mealRepository.findById(1L)).thenReturn(Optional.of(existingMeal));
        when(mealRepository.save(any(Meal.class))).thenReturn(existingMeal);

        Optional<Meal> result = mealService.patchMeal(1L,patchMeal);

        assertTrue(result.isPresent());
        assertEquals("Updated Name", result.get().getName());
        assertEquals("BREAKFAST",result.get().getMealType());
    }
    @Test
    void testUpdateMeal_NotFound(){
        when(mealRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Meal> result = mealService.updateMeal(1L,new Meal());
        assertTrue(result.isEmpty());

    }
}
