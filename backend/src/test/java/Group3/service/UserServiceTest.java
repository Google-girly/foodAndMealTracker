package Group3.service;

import Group3.model.User;
import Group3.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User(), new User()));
        List<User> users = userService.getAllUsers();
        assertEquals(3, users.size());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserById() {
        User u = new User();
        u.setEmail("a@b.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("a@b.com", result.get().getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void testCreateUser() {
        User toCreate = new User();
        toCreate.setEmail("new@user.com");
        when(userRepository.save(any(User.class))).thenReturn(toCreate);

        User created = userService.createUser(toCreate);

        assertEquals("new@user.com", created.getEmail());
        verify(userRepository).save(toCreate);
    }

    @Test
    void testUpdateUser_Success_updatesAllFields() {
        User existing = new User();
        existing.setEmail("old@user.com");
        existing.setFullName("Old Name");
        existing.setAdmin(false);

        User updated = new User();
        updated.setEmail("new@user.com");
        updated.setFullName("New Name");
        updated.setAdmin(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<User> result = userService.updateUser(1L, updated);

        assertTrue(result.isPresent());
        User saved = result.get();
        assertEquals("new@user.com", saved.getEmail());
        assertEquals("New Name", saved.getFullName());
        assertTrue(saved.getAdmin());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertSame(existing, captor.getValue());
    }

    @Test
    void testUpdateUser_NotFound_returnsEmptyAndDoesNotSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.updateUser(1L, new User());

        assertTrue(result.isEmpty());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testPatchUser_Success_onlyUpdatesProvidedFields() {
        User existing = new User();
        existing.setEmail("old@user.com");
        existing.setFullName("Old Name");
        existing.setAdmin(false);

        User patch = new User();
        patch.setFullName("Patched Name"); // only patch full name

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<User> result = userService.patchUser(1L, patch);

        assertTrue(result.isPresent());
        User saved = result.get();
        assertEquals("old@user.com", saved.getEmail()); // unchanged
        assertEquals("Patched Name", saved.getFullName());
        assertFalse(saved.getAdmin()); // unchanged
    }

    @Test
    void testPatchUser_NotFound_returnsEmptyAndDoesNotSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.patchUser(1L, new User());

        assertTrue(result.isEmpty());
        verify(userRepository, never()).save(any());
    }
}
