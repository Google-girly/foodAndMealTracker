package Group3.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
/**
 * Represents the relationship between a meal and a food item.
 *
 * This entity stores which food belongs to a meal,
 * along with quantity and unit information.
 */
@Entity
@Table(name = "meal_foods")
public class MealFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the associated meal.
     */
    // Raw FK ids for simple API usage
    @Column(name = "meal_id", nullable = false)
    private Long mealId;
    /**
     * ID of the associated food item.
     */
    @Column(name = "food_id", nullable = false)
    private Long foodId;
    /**
     * Reference to the meal (read-only relationship).
     */
    // Relationships (read-only, mapped to same columns)
    @ManyToOne
    @JoinColumn(name = "meal_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Meal meal;
    /**
     * Reference to the food (read-only relationship).
     */
    @ManyToOne
    @JoinColumn(name = "food_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Food food;
    /**
     * Quantity of the food in the meal.
     */
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;
    /**
     * Unit of measurement (e.g., grams, cups).
     */
    @Column(nullable = false, length = 50)
    private String unit;
    /**
     * Default constructor for MealFood.
     */
    public MealFood() {}
     /**
     * Gets the ID of the meal-food relationship.
     *
     * @return ID
     */
    public Long getId() { return id; }
    /**
     * Gets the meal ID.
     *
     * @return meal ID
     */
    public Long getMealId() { return mealId; }
    /**
     * Sets the meal ID.
     *
     * @param mealId meal ID
     */
    public void setMealId(Long mealId) { this.mealId = mealId; }
    /**
     * Gets the food ID.
     *
     * @return food ID
     */
    public Long getFoodId() { return foodId; }
    /**
     * Sets the food ID.
     *
     * @param foodId food ID
     */
    public void setFoodId(Long foodId) { this.foodId = foodId; }
    /**
     * Gets the associated meal.
     *
     * @return meal entity
     */
    public Meal getMeal() { return meal; }
        /**
        * Sets the associated meal.
        *
        * @param meal meal entity
        */
    public void setMeal(Meal meal) { this.meal = meal; }
    /**
     * Gets the associated food.
     *
     * @return food entity
     */
    public Food getFood() { return food; }
    /**
     * Sets the associated food.
     *
     * @param food food entity
     */
    public void setFood(Food food) { this.food = food; }
    /**
     * Gets the quantity of the food.
     *
     * @return quantity
     */
    public BigDecimal getQuantity() { return quantity; }
    /**
     * Sets the quantity of the food.
     *
     * @param quantity quantity to set
     */
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    /**
     * Gets the unit of measurement.
     *
     * @return unit
     */
    public String getUnit() { return unit; }
    /**
     * Sets the unit of measurement.
     *
     * @param unit unit to set
     */
    public void setUnit(String unit) { this.unit = unit; }
}
