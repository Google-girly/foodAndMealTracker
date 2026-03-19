package Group3.repository;

import Group3.model.MealFood;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing MealFood entities.
 *
 * Provides standard CRUD operations and database access
 * methods through Spring Data JPA.
 */
public interface MealFoodRepository extends JpaRepository<MealFood, Long> {
}
