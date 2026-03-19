package Group3.service;

import Group3.model.MealFood;
import Group3.repository.MealFoodRepository;
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
 * Unit tests for the MealFoodService class.
 *
 * Verifies CRUD operations and behavior using mocked MealFoodRepository.
 */
public class MealFoodServiceTest {

    private MealFoodRepository mealFoodRepository;
    private MealFoodService mealFoodService;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        mealFoodRepository = Mockito.mock(MealFoodRepository.class);
        mealFoodService = new MealFoodService(mealFoodRepository);
    }

    /**
     * Tests retrieving all meal-food entries.
     */
    @Test
    void testGetAllMealFoods() {
        when(mealFoodRepository.findAll()).thenReturn(List.of(new MealFood(), new MealFood()));
        List<MealFood> mfs = mealFoodService.getAllMealFoods();
        assertEquals(2, mfs.size());
        verify(mealFoodRepository).findAll();
    }

    /**
     * Tests retrieving a meal-food entry by ID.
     */
    @Test
    void testGetMealFoodById() {
        MealFood mf = new MealFood();
        mf.setUnit("g");
        when(mealFoodRepository.findById(1L)).thenReturn(Optional.of(mf));

        Optional<MealFood> result = mealFoodService.getMealFoodById(1L);

        assertTrue(result.isPresent());
        assertEquals("g", result.get().getUnit());
        verify(mealFoodRepository).findById(1L);
    }

    /**
     * Tests creating a new meal-food entry.
     */
    @Test
    void testCreateMealFood() {
        MealFood toCreate = new MealFood();
        toCreate.setMealId(1L);
        toCreate.setFoodId(2L);
        toCreate.setQuantity(new BigDecimal("1.250"));
        toCreate.setUnit("cups");

        when(mealFoodRepository.save(any(MealFood.class))).thenReturn(toCreate);

        MealFood created = mealFoodService.createMealFood(toCreate);

        assertEquals(1L, created.getMealId());
        assertEquals(2L, created.getFoodId());
        assertEquals(new BigDecimal("1.250"), created.getQuantity());
        assertEquals("cups", created.getUnit());
        verify(mealFoodRepository).save(toCreate);
    }

    /**
     * Tests successful update of a meal-food entry.
     */
    @Test
    void testUpdateMealFood_Success_updatesAllFields() {
        MealFood existing = new MealFood();
        existing.setMealId(1L);
        existing.setFoodId(2L);
        existing.setQuantity(new BigDecimal("1.000"));
        existing.setUnit("g");

        MealFood updated = new MealFood();
        updated.setMealId(10L);
        updated.setFoodId(20L);
        updated.setQuantity(new BigDecimal("2.500"));
        updated.setUnit("oz");

        when(mealFoodRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(mealFoodRepository.save(any(MealFood.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<MealFood> result = mealFoodService.updateMealFood(1L, updated);

        assertTrue(result.isPresent());
        MealFood saved = result.get();
        assertEquals(10L, saved.getMealId());
        assertEquals(20L, saved.getFoodId());
        assertEquals(new BigDecimal("2.500"), saved.getQuantity());
        assertEquals("oz", saved.getUnit());

        ArgumentCaptor<MealFood> captor = ArgumentCaptor.forClass(MealFood.class);
        verify(mealFoodRepository).save(captor.capture());
        assertSame(existing, captor.getValue());
    }

    /**
     * Tests update behavior when entry is not found.
     */
    @Test
    void testUpdateMealFood_NotFound_returnsEmptyAndDoesNotSave() {
        when(mealFoodRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<MealFood> result = mealFoodService.updateMealFood(1L, new MealFood());

        assertTrue(result.isEmpty());
        verify(mealFoodRepository, never()).save(any());
    }

    /**
     * Tests partial update of a meal-food entry.
     */
    @Test
    void testPatchMealFood_Success_onlyUpdatesProvidedFields() {
        MealFood existing = new MealFood();
        existing.setMealId(1L);
        existing.setFoodId(2L);
        existing.setQuantity(new BigDecimal("1.000"));
        existing.setUnit("g");

        MealFood patch = new MealFood();
        patch.setQuantity(new BigDecimal("3.000")); // only patch quantity

        when(mealFoodRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(mealFoodRepository.save(any(MealFood.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<MealFood> result = mealFoodService.patchMealFood(1L, patch);

        assertTrue(result.isPresent());
        MealFood saved = result.get();
        assertEquals(1L, saved.getMealId());
        assertEquals(2L, saved.getFoodId());
        assertEquals(new BigDecimal("3.000"), saved.getQuantity());
        assertEquals("g", saved.getUnit());
    }

    /**
     * Tests patch behavior when entry is not found.
     */
    @Test
    void testPatchMealFood_NotFound_returnsEmptyAndDoesNotSave() {
        when(mealFoodRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<MealFood> result = mealFoodService.patchMealFood(1L, new MealFood());

        assertTrue(result.isEmpty());
        verify(mealFoodRepository, never()).save(any());
    }
}
