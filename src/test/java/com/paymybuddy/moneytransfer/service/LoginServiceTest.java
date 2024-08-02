package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccessful() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        Optional<User> result = loginService.login("testuser", "password");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password", "encodedPassword");
    }

    @Test
    public void testLoginPasswordMismatch() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        Optional<User> result = loginService.login("testuser", "wrongpassword");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
    }

    @Test
    public void testLoginUserNotFound() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(null);

        Optional<User> result = loginService.login("nonexistentuser", "password");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("nonexistentuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}
