package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.model.UserConnection;
import com.paymybuddy.moneytransfer.repository.UserConnectionRepository;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserConnectionServiceTest {

    @Mock
    private UserConnectionRepository userConnectionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserConnectionService userConnectionService;

    public UserConnectionServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddConnection_Success() {
        User user = new User();
        User connectedUser = new User();
        when(userRepository.findByUsername(any(String.class))).thenReturn(user);
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(connectedUser));
        when(userConnectionRepository.existsByUserAndConnectedUser(any(User.class), any(User.class))).thenReturn(false);

        userConnectionService.addConnection("username", "email@example.com");

        verify(userConnectionRepository).save(any(UserConnection.class));
    }

    @Test
    void testAddConnection_UserNotFound() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            userConnectionService.addConnection("username", "email@example.com");
        });
    }

    @Test
    void testAddConnection_EmailNotFound() {
        User user = new User();
        when(userRepository.findByUsername(any(String.class))).thenReturn(user);
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userConnectionService.addConnection("username", "email@example.com");
        });
    }

    @Test
    void testConnectionExists_Success() {
        User user = new User();
        User connectedUser = new User();
        when(userRepository.findByUsername(any(String.class))).thenReturn(user);
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(connectedUser));
        when(userConnectionRepository.existsByUserAndConnectedUser(any(User.class), any(User.class))).thenReturn(true);

        boolean exists = userConnectionService.connectionExists("username", "email@example.com");

        assertTrue(exists);
    }

    @Test
    void testConnectionExists_UserOrConnectedUserNull() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(null);
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        boolean exists = userConnectionService.connectionExists("username", "email@example.com");

        assertFalse(exists);
    }
}
