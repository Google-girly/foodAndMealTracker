package Group3.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Group3.model.User;
import Group3.service.UserService;

/**
 * Controller responsible for handling operations related to users.
 * 
 * Provides endpoints to:
 * - Retrieve all users
 * - Retrieve a user by ID
 * - Create new user entries
 * - Update or partially update existing users
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    /**
     * Constructs a UserController with the given UserService.
     *
     * @param userService service used to manage user data
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all users.
     *
     * @return list of all users
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    /**
     * Retrieves a specific user by their ID.
     *
     * @param id the ID of the user
     * @return the user if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    /**
     * Creates a new user entry.
     *
     * @param user the user data to create
     * @return created user with 201 status
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.status(201).body(created);
    }
    /**
     * Updates an existing user entry completely.
     *
     * @param id the ID of the user to update
     * @param updatedUser the updated user data
     * @return updated user if found, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userService.updateUser(id, updatedUser).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    /**
 * Partially updates an existing user.
 *
 * @param id the ID of the user to update
 * @param partialUser the partial user data
 * @return updated user if found, or 404 if not found
 */
    @PatchMapping("/{id}")
    public ResponseEntity<User> patchUser(@PathVariable Long id, @RequestBody User partialUser) {
        return userService.patchUser(id, partialUser).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
