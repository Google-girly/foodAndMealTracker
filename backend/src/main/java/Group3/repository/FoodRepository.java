package Group3.repository;

import Group3.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Food entities.
 *
 * Provides standard CRUD operations and database access
 * methods through Spring Data JPA.
 */
public interface FoodRepository extends JpaRepository<Food, Long> {
}
