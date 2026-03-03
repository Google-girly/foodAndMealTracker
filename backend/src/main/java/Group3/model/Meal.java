package Group3.model;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //Based on ERD
    private Long usersId;
    private String name;
    private String mealType;
    private LocalDate mealDate;
    private String description;
    

    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Meal(){}

    public Long getId(){
        return id;
    }
    public Long getUsersId(){
        return usersId;
    }
    public void setUsersId(Long usersId){
        this.usersId = usersId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getMealType(){
        return mealType;
    }
    public void setMealType(String mealType){
        this.mealType = mealType;
    }
    public LocalDate getMealDate(){
        return mealDate;
    }
    public void setMealDate(LocalDate mealDate){
        this.mealDate = mealDate;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String Description){
        this.description = Description;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt){
        this.updatedAt = updatedAt;
    }

}
