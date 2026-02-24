package Group3.repository;

import Group3.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MealRepository extends JpaRepository<Meal, Long> {
    
}
