package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.model.UserConnection;
import com.paymybuddy.moneytransfer.repository.UserConnectionRepository;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import com.paymybuddy.moneytransfer.service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UserConnectionRepository userConnectionRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.test");
        user.setPassword("password123@");

        when(bCryptPasswordEncoder.encode("password123@")).thenReturn("encodedPassword");

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    public void testUsernameExists() {
        when(userRepository.findByUsername("existingUser")).thenReturn(new User());

        assertTrue(userService.usernameExists("existingUser"));
        assertFalse(userService.usernameExists("nonExistingUser"));

        verify(userRepository, times(1)).findByUsername("existingUser");
        verify(userRepository, times(1)).findByUsername("nonExistingUser");
    }

    @Test
    public void testEmailExists() {
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        assertTrue(userService.emailExists("existing@example.com"));
        assertFalse(userService.emailExists("nonexisting@example.com"));

        verify(userRepository, times(1)).findByEmail("existing@example.com");
        verify(userRepository, times(1)).findByEmail("nonexisting@example.com");
    }

    @Test
    public void testGetUserByUsername() {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        User result = userService.getUserByUsername("testuser");

        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    public void testGetUserByUserId() {
        User user = new User();
        user.setUserID(1);

        when(userRepository.findByUserID(1)).thenReturn(user);

        User result = userService.getUserByUserId(1);

        assertEquals(1, result.getUserID());
        verify(userRepository, times(1)).findByUserID(1);
    }

    @Test
    public void testGetUserByEmail() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("test@example.com");

        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testGetUserByEmailNotFound() {
        when(userRepository.findByEmail("nonexisting@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.getUserByEmail("nonexisting@example.com");
        });

        verify(userRepository, times(1)).findByEmail("nonexisting@example.com");
    }

    @Test
    public void testUpdateUserDetails() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("oldUsername");
        user.setEmail("old@example.com");
        user.setPassword("oldPassword123@");

        when(userRepository.findByUserID(1)).thenReturn(user);
        when(bCryptPasswordEncoder.encode("newPassword123@")).thenReturn("encodedNewPassword");

        userService.updateUserDetails(1, "newUsername", "new@example.com", "newPassword123@");

        verify(userRepository, times(1)).save(user);
        assertEquals("newUsername", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    public void testUpdateUserDetailsUserNotFound() {
        when(userRepository.findByUserID(1)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUserDetails(1, "newUsername", "new@example.com", "newPassword");
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUserDetailsEmptyPassword() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("username");
        user.setEmail("test@example.com");
        user.setPassword("password123@");

        when(userRepository.findByUserID(1)).thenReturn(user);

        userService.updateUserDetails(1, "newUsername", "new@example.com", "");

        verify(userRepository, times(1)).save(user);
        assertEquals("newUsername", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("password123@", user.getPassword());
    }

    @Test
    public void testUpdateUserDetailsNullPassword() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("username");
        user.setEmail("test@example.com");
        user.setPassword("password123@");

        when(userRepository.findByUserID(1)).thenReturn(user);

        userService.updateUserDetails(1, "newUsername", "new@example.com", null);

        verify(userRepository, times(1)).save(user);
        assertEquals("newUsername", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("password123@", user.getPassword());
    }

    @Test
    public void testGetUserConnections() {
        User user = new User();
        user.setUsername("testuser");

        UserConnection connection = new UserConnection();
        connection.setConnectedUser(new User());

        when(userConnectionRepository.findByUser(user)).thenReturn(Collections.singletonList(connection));

        List<User> connections = userService.getUserConnections(user);

        assertEquals(1, connections.size());
        verify(userConnectionRepository, times(1)).findByUser(user);
    }
}