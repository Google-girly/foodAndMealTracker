package Group3.repository;

import Group3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing User entities.
 *
 * Provides standard CRUD operations and database access
 * methods through Spring Data JPA.
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
