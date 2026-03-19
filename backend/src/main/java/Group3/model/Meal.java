package Group3.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
/**
 * Represents a meal created by a user.
 *
 * A meal contains metadata such as name, type, date,
 * and a collection of associated food items.
 */
@Entity
@Table(name = "meals")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the user who owns this meal.
     */
    // Keep the raw FK id for your current controller/service style
    @Column(name = "users_id")
    private Long usersId;

    // Also expose the relationship (read-only, mapped to the same column)
    /**
     * Reference to the associated user (read-only relationship).
     */
    @ManyToOne
    @JoinColumn(name = "users_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private User user;

    private String name;

    @Column(name = "meal_type")
    private String mealType;

    @Column(name = "meal_date")
    private LocalDate mealDate;

    private String description;
   
   /**
     * Timestamp when the meal was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
        /**
        * Timestamp when the meal was last updated.
        */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    /**
     * List of foods associated with this meal.
     */
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MealFood> mealFoods;
    /**
     * Default constructor for Meal.
     */
    public Meal() {}
     /**
     * Gets the meal ID.
     *
     * @return meal ID
     */
    public Long getId() { return id; }
    /**
     * Gets the user ID associated with the meal.
     *
     * @return user ID
     */
    public Long getUsersId() { return usersId; }
    /**
     * Sets the user ID for the meal.
     *
     * @param usersId the user ID to set
     */
    public void setUsersId(Long usersId) { this.usersId = usersId; }
    /**
     * Gets the associated user.
     *
     * @return user entity
     */
    public User getUser() { return user; }
    /**
     * Sets the associated user (read-only, does not update usersId).
     *
     * @param user the user entity to associate
     */
    public void setUser(User user) { this.user = user; }
    /**
     * Gets the meal name.
     *
     * @return meal name
     */
    public String getName() { return name; }
    /**
     * Sets the meal name.
     *
     * @param name the meal name to set
     */
    public void setName(String name) { this.name = name; }
    /**
     * Gets the meal type (e.g., breakfast, lunch).
     *
     * @return meal type
     */
    public String getMealType() { return mealType; }
    /**
     * Sets the meal type (e.g., breakfast, lunch).
     *
     * @param mealType the meal type to set
     */
    public void setMealType(String mealType) { this.mealType = mealType; }
    /**
 * Gets the meal date.
 *
 * @return meal date
 */
    public LocalDate getMealDate() { return mealDate; }
    /**
     * Sets the meal date.
     *
     * @param mealDate the date of the meal
     */
    public void setMealDate(LocalDate mealDate) { this.mealDate = mealDate; }
    /**
 * Gets the meal description.
 *
 * @return description
 */
    public String getDescription() { return description; }
    /**
     * Sets the meal description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) { this.description = description; }
        /**
        * Gets the creation timestamp of the meal.
        *
        * @return creation timestamp
        */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * Sets the creation timestamp of the meal.
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    /**
     * Gets the last updated timestamp of the meal.
     *
     * @return last updated timestamp
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /**
     * Sets the last updated timestamp of the meal.
     *
     * @param updatedAt the last updated timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        /**
        * Gets the list of meal-food relationships associated with this meal.
        *
        * @return list of meal-food entries
        */
    public List<MealFood> getMealFoods() { return mealFoods; }
    /**
     * Sets the list of meal-food relationships associated with this meal.
     *
     * @param mealFoods the list of meal-food entries to set
     */
    public void setMealFoods(List<MealFood> mealFoods) { this.mealFoods = mealFoods; }
}
