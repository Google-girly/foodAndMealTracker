package Group3.service;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import Group3.model.Meal;
import Group3.repository.MealRepository;

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
    @Test
    void testUpdateMeal_NotFound(){
        Long usersId = 1L;
        when(mealRepository.findByIdAndUsersId(1L, usersId)).thenReturn(Optional.empty());
        Optional<Meal> result = mealService.updateMeal(1L,new Meal(), usersId);
        assertTrue(result.isEmpty());
    }
    @Test
    void testUpdateMeal_UserDoesNotOwnMeal_shouldReturnEmpty(){
        Long mealId = 1L;
        long userId = 1L;

        when(mealRepository.findByIdAndUsersId(mealId, userId)).thenReturn(Optional.empty());

        Optional<Meal> result = mealService.updateMeal(mealId, new Meal(),userId);

        assertTrue(result.isEmpty());
        verify(mealRepository, never()).save(any());

    }
    @Test
    void testPatchMeal_UserDoesNotOwnMeal_shouldReturnEmpty(){
        Long mealId = 1L;
        long userId = 1L;

        when(mealRepository.findByIdAndUsersId(mealId, userId)).thenReturn(Optional.empty());

        Optional<Meal> result = mealService.patchMeal(mealId, new Meal(),userId);

        assertTrue(result.isEmpty());
        verify(mealRepository, never()).save(any());
    }
}
