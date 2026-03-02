package Group3.repository;

import Group3.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findAllByUsersId(Long usersId);
    Optional<Meal> findByIdAndUsersId(Long id, Long usersId);
}