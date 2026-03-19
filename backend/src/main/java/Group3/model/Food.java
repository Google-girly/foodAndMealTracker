package Group3.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

/**
 * Represents a food item with nutritional information.
 *
 * This entity stores details such as calories, macronutrients,
 * creator information, and visibility status.
 */
@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    private Integer calories;

    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fat;

    // Keep the raw FK id for simple controller/service usage
    /**
     * ID of the user who created this food item.
     */
    @Column(name = "created_by")
    private Long createdById;

    /**
     * Indicates whether this food is publicly visible.
     */
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    /**
     * Timestamp of when the food item was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    /**
     * Default constructor for Food.
     */
    public Food() {}
    /**
     * Gets the unique ID of the food.
     *
     * @return the food ID
     */
    public Long getId() { return id; }
    /**
     * Gets the name of the food.
     *
     * @return the food name
     */
    public String getName() { return name; }
    /**
     * Sets the name of the food.
     *
     * @param name the food name
     */
    public void setName(String name) { this.name = name; }
    /**
     * Gets the calorie count.
     *
     * @return calories
     */
    public Integer getCalories() { return calories; }
    /**
     * Sets the calorie count.
     *
     * @param calories number of calories
     */
    public void setCalories(Integer calories) { this.calories = calories; }
    /**
     * Gets protein content.
     *
     * @return protein amount
     */
    public BigDecimal getProtein() { return protein; }
    /**
     * Sets the protein content.
     *
     * @param protein amount of protein
     */
    public void setProtein(BigDecimal protein) { this.protein = protein; }
    /**
     * Gets carbohydrate content.
     *
     * @return carbohydrate amount
     */
    public BigDecimal getCarbs() { return carbs; }
    /**
     * Sets the carbohydrate content.
     *
     * @param carbs amount of carbohydrates
     */
    public void setCarbs(BigDecimal carbs) { this.carbs = carbs; }
    /**
     * Gets fat content.
     *
     * @return fat amount
     */
    public BigDecimal getFat() { return fat; }
    /**
     * Sets the fat content.
     *
     * @param fat amount of fat
     */
    public void setFat(BigDecimal fat) { this.fat = fat; }
    /**
     * Gets the creator's user ID.
     *
     * @return creator user ID
     */
    public Long getCreatedById() { return createdById; }
    /**
     * Sets the creator's user ID.
     *
     * @param createdById the user ID of the creator
     */
    public void setCreatedById(Long createdById) { this.createdById = createdById; }
    /**
     * Checks if the food is public.
     *
     * @return true if public, false otherwise
     */
    public Boolean getIsPublic() { return isPublic; }
     /**
     * Sets whether the food is public.
     *
     * @param isPublic visibility status
     */
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    /**
     * Gets the creation timestamp.
     *
     * @return creation time
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * Sets the creation timestamp.
     *
     * @param createdAt the time when the food was created
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
