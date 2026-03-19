package Group3.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

/**
 * Represents a user in the system.
 *
 * Stores user identity, profile information,
 * administrative status, and creation timestamp.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique email address of the user.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    /**
     * Full name of the user.
     */
    @Column(name = "full_name", length = 255)
    private String fullName;
    /**
     * Indicates whether the user has admin privileges.
     */
    @Column(nullable = false)
    private Boolean admin = false;
    /**
     * Timestamp of when the user account was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    /**
     * Default constructor for User.
     */
    public User() {}
    /**
     * Gets the user ID.
     *
     * @return user ID
     */
    public Long getId() { return id; }
    /**
     * Gets the user's email address.
     *
     * @return email address
     */
    public String getEmail() { return email; }
    /**
     * Sets the user's email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) { this.email = email; }
    /**
     * Gets the user's full name.
     *
     * @return full name
     */
    public String getFullName() { return fullName; }
    /**
     * Sets the user's full name.
     *
     * @param fullName the full name to set
     */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /**
     * Gets the admin status of the user.
     *
     * @return admin status
     */
    public Boolean getAdmin() { return admin; }
    // public void setAdmin(Boolean admin) { this.admin = admin; }

    /**
     * Gets the creation timestamp of the user.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * Sets the creation timestamp of the user.
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
