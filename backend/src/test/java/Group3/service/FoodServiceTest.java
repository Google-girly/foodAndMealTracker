package Group3.service;

import Group3.model.Food;
import Group3.repository.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the FoodService class.
 *
 * Verifies CRUD operations and behavior using mocked FoodRepository.
 */
public class FoodServiceTest {

    private FoodRepository foodRepository;
    private FoodService foodService;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        foodRepository = Mockito.mock(FoodRepository.class);
        foodService = new FoodService(foodRepository);
    }

    /**
     * Tests retrieving all food items.
     */
    @Test
    void testGetAllFoods() {
        when(foodRepository.findAll()).thenReturn(List.of(new Food(), new Food()));
        List<Food> foods = foodService.getAllFoods();
        assertEquals(2, foods.size());
        verify(foodRepository, times(1)).findAll();
    }

    /**
     * Tests retrieving a food item by ID.
     */
    @Test
    void testGetFoodById() {
        Food f = new Food();
        f.setName("Apple");
        when(foodRepository.findById(1L)).thenReturn(Optional.of(f));

        Optional<Food> result = foodService.getFoodById(1L);

        assertTrue(result.isPresent());
        assertEquals("Apple", result.get().getName());
        verify(foodRepository).findById(1L);
    }

    /**
     * Tests creating a new food item.
     */
    @Test
    void testCreateFood() {
        Food toCreate = new Food();
        toCreate.setName("Banana");
        when(foodRepository.save(any(Food.class))).thenReturn(toCreate);

        Food created = foodService.createFood(toCreate);

        assertEquals("Banana", created.getName());
        verify(foodRepository).save(toCreate);
    }

    /**
     * Tests successful update of a food item.
     */
    @Test
    void testUpdateFood_Success_updatesAllFields() {
        Food existing = new Food();
        existing.setName("Old");
        existing.setCalories(100);
        existing.setProtein(new BigDecimal("1.00"));
        existing.setCarbs(new BigDecimal("2.00"));
        existing.setFat(new BigDecimal("3.00"));
        existing.setCreatedById(9L);
        existing.setIsPublic(false);

        Food updated = new Food();
        updated.setName("New");
        updated.setCalories(200);
        updated.setProtein(new BigDecimal("4.50"));
        updated.setCarbs(new BigDecimal("5.50"));
        updated.setFat(new BigDecimal("6.50"));
        updated.setCreatedById(10L);
        updated.setIsPublic(true);

        when(foodRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(foodRepository.save(any(Food.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Food> result = foodService.updateFood(1L, updated);

        assertTrue(result.isPresent());
        Food saved = result.get();
        assertEquals("New", saved.getName());
        assertEquals(200, saved.getCalories());
        assertEquals(new BigDecimal("4.50"), saved.getProtein());
        assertEquals(new BigDecimal("5.50"), saved.getCarbs());
        assertEquals(new BigDecimal("6.50"), saved.getFat());
        assertEquals(10L, saved.getCreatedById());
        assertTrue(saved.getIsPublic());

        // Ensure we saved the mutated entity
        ArgumentCaptor<Food> captor = ArgumentCaptor.forClass(Food.class);
        verify(foodRepository).save(captor.capture());
        assertSame(existing, captor.getValue());
    }

    /**
     * Tests update behavior when food is not found.
     */
    @Test
    void testUpdateFood_NotFound_returnsEmptyAndDoesNotSave() {
        when(foodRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Food> result = foodService.updateFood(1L, new Food());

        assertTrue(result.isEmpty());
        verify(foodRepository, never()).save(any());
    }

    /**
     * Tests partial update of a food item.
     */
    @Test
    void testPatchFood_Success_onlyUpdatesProvidedFields() {
        Food existing = new Food();
        existing.setName("Old");
        existing.setCalories(100);
        existing.setProtein(new BigDecimal("1.00"));
        existing.setCarbs(new BigDecimal("2.00"));
        existing.setFat(new BigDecimal("3.00"));
        existing.setCreatedById(9L);
        existing.setIsPublic(false);

        Food patch = new Food();
        patch.setName("Patched Name");
        patch.setCalories(150);
        // leave everything else null

        when(foodRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(foodRepository.save(any(Food.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Food> result = foodService.patchFood(1L, patch);

        assertTrue(result.isPresent());
        Food saved = result.get();
        assertEquals("Patched Name", saved.getName());
        assertEquals(150, saved.getCalories());
        // unchanged
        assertEquals(new BigDecimal("1.00"), saved.getProtein());
        assertEquals(new BigDecimal("2.00"), saved.getCarbs());
        assertEquals(new BigDecimal("3.00"), saved.getFat());
        assertEquals(9L, saved.getCreatedById());
        assertFalse(saved.getIsPublic());
    }

    /**
     * Tests patch behavior when food is not found.
     */
    @Test
    void testPatchFood_NotFound_returnsEmptyAndDoesNotSave() {
        when(foodRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Food> result = foodService.patchFood(1L, new Food());

        assertTrue(result.isEmpty());
        verify(foodRepository, never()).save(any());
    }
}
