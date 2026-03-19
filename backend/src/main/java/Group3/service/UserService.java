package Group3.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import Group3.model.User;
import Group3.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // public User createUser(User user) {
    //     return userRepository.save(user);
    // }

    public Optional<User> updateUser(Long id, User updatedUser) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        User user = optional.get();
        user.setEmail(updatedUser.getEmail());
        user.setFullName(updatedUser.getFullName());
        user.setAdmin(updatedUser.getAdmin());
        return Optional.of(userRepository.save(user));
    }

    public Optional<User> patchUser(Long id, User partialUser) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty()) return Optional.empty();

        User user = optional.get();

        if (partialUser.getEmail() != null) user.setEmail(partialUser.getEmail());
        if (partialUser.getFullName() != null) user.setFullName(partialUser.getFullName());
        if (partialUser.getAdmin() != null) user.setAdmin(partialUser.getAdmin());

        return Optional.of(userRepository.save(user));
    }

    public User createUser(User user){
        if (user.getAdmin() == null){
            user.setAdmin(false);
        }
        return userRepository.save(user);
    }
}
