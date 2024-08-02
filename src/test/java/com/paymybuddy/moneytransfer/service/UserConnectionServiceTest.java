package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.model.UserConnection;
import com.paymybuddy.moneytransfer.repository.UserConnectionRepository;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserConnectionServiceTest {

    @Mock
    private UserConnectionRepository userConnectionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserConnectionService userConnectionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddConnection() {
        User currentUser = new User();
        currentUser.setUsername("testuser");
        User connectedUser = new User();
        connectedUser.setEmail("test@mail.com");

        when(userRepository.findByUsername("testuser")).thenReturn(currentUser);
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(connectedUser));

        userConnectionService.addConnection("testuser", "test@mail.com");

        verify(userConnectionRepository, times(1)).save(any(UserConnection.class));
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).findByEmail("test@mail.com");
    }

    @Test
    public void testAddConnectionEmailNotFound() {
        User currentUser = new User();
        currentUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(currentUser);
        when(userRepository.findByEmail("nonexistent@mail.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userConnectionService.addConnection("testuser", "nonexistent@mail.com");
        });

        assertEquals("Email not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).findByEmail("nonexistent@mail.com");
        verify(userConnectionRepository, never()).save(any(UserConnection.class));
    }

    @Test
    public void testConnectionExists() {
        User currentUser = new User();
        currentUser.setUsername("testuser");
        User connectedUser = new User();
        connectedUser.setEmail("test@mail.com");

        when(userRepository.findByUsername("testuser")).thenReturn(currentUser);
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(connectedUser));
        when(userConnectionRepository.existsByUserAndConnectedUser(currentUser, connectedUser)).thenReturn(true);

        boolean exists = userConnectionService.connectionExists("testuser", "test@mail.com");

        assertTrue(exists);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).findByEmail("test@mail.com");
        verify(userConnectionRepository, times(1)).existsByUserAndConnectedUser(currentUser, connectedUser);
    }

    @Test
    public void testConnectionDoesNotExist() {
        User currentUser = new User();
        currentUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(currentUser);
        when(userRepository.findByEmail("nonexistent@mail.com")).thenReturn(Optional.empty());

        boolean exists = userConnectionService.connectionExists("testuser", "nonexistent@mail.com");

        assertFalse(exists);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).findByEmail("nonexistent@mail.com");
        verify(userConnectionRepository, never()).existsByUserAndConnectedUser(any(User.class), any(User.class));
    }
}
