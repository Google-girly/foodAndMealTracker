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

/**
 * Unit tests for the MealService class.
 *
 * Verifies update and patch operations using a mocked MealRepository.
 */
public class MealServiceTest {
    private MealRepository mealRepository;
    private MealService mealService;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp(){
        mealRepository = Mockito.mock(MealRepository.class);
        mealService = new MealService(mealRepository);
    }

    /**
     * Tests successful full update (PUT) of a meal.
     */
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

        Long usersId = 1L;
        when(mealRepository.findByIdAndUsersId(1L, usersId)).thenReturn(Optional.of(existingMeal));
        when(mealRepository.save(any(Meal.class))).thenAnswer(inv -> inv.getArgument(0));
        Optional<Meal> result = mealService.updateMeal(1L,updatedMeal, usersId);
        
        assertTrue(result.isPresent());
        assertEquals("New Name",result.get().getName());
        assertEquals("DINNER", result.get().getMealType());
        assertEquals("New Description", result.get().getDescription());
        assertEquals(usersId, result.get().getUsersId());
    }

    /**
     * Tests successful partial update (PATCH) of a meal.
     */
    @Test
    void testPatchMeal_Success(){
        Meal existingMeal = new Meal();
        existingMeal.setName("Original name");
        existingMeal.setMealType("BREAKFAST");

        Meal patchMeal = new Meal();
        patchMeal.setName("Updated Name");

        Long usersId = 1L;
        when(mealRepository.findByIdAndUsersId(1L, usersId)).thenReturn(Optional.of(existingMeal));
        when(mealRepository.save(any(Meal.class))).thenAnswer(inv -> inv.getArgument(0));
        Optional<Meal> result = mealService.patchMeal(1L,patchMeal, usersId);

        assertTrue(result.isPresent());
        assertEquals("Updated Name", result.get().getName());
        assertEquals("BREAKFAST",result.get().getMealType());
        assertEquals(usersId, result.get().getUsersId());
    }

    /**
     * Tests update behavior when the meal is not found.
     */
    @Test
    void testUpdateMeal_NotFound(){
        Long usersId = 1L;
        when(mealRepository.findByIdAndUsersId(1L, usersId)).thenReturn(Optional.empty());
        Optional<Meal> result = mealService.updateMeal(1L,new Meal(), usersId);
        assertTrue(result.isEmpty());
    }
}
