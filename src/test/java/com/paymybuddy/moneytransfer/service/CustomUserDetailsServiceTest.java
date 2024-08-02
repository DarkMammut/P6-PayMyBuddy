package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists() {
        // Arrange
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);

        when(userRepository.findByUsername(username)).thenReturn(user);

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        // Arrange
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_UserWithoutRoles() {
        // Arrange
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setRoles(Collections.emptySet());

        when(userRepository.findByUsername(username)).thenReturn(user);

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(userRepository, times(1)).findByUsername(username);
    }
}