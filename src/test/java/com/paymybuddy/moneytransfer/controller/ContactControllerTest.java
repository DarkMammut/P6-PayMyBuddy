package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.model.UserConnection;
import com.paymybuddy.moneytransfer.service.UserConnectionService;
import com.paymybuddy.moneytransfer.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ContactControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserConnectionService userConnectionService;

    @Mock
    private UserDetails currentUser;

    @Mock
    private Model model;

    @InjectMocks
    private ContactController contactController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowAddContactForm_UserExists() {
        User user = new User();
        user.setUsername("currentUser");

        when(currentUser.getUsername()).thenReturn("currentUser");
        when(userService.getUserByUsername(anyString())).thenReturn(user);

        String viewName = contactController.showAddContactForm(currentUser, model);

        verify(model, times(1)).addAttribute(eq("userConnection"), any(UserConnection.class));
        assertEquals("contact", viewName);
    }

    @Test
    public void testShowAddContactForm_UserNotFound() {
        when(currentUser.getUsername()).thenReturn("currentUser");
        when(userService.getUserByUsername(anyString())).thenReturn(null);

        String viewName = contactController.showAddContactForm(currentUser, model);

        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testAddContact_UserAndConnectedUserExist() {
        User user = new User();
        user.setUsername("currentUser");
        User connectedUser = new User();
        connectedUser.setEmail("connectedUser@example.com");

        when(currentUser.getUsername()).thenReturn("currentUser");
        when(userService.getUserByUsername(anyString())).thenReturn(user);
        when(userService.getUserByEmail(anyString())).thenReturn(connectedUser);

        String viewName = contactController.addContact(currentUser, "connectedUser@example.com");

        verify(userConnectionService, times(1)).addConnection("currentUser", "connectedUser@example.com");
        assertEquals("redirect:/contact?success", viewName);
    }

    @Test
    public void testAddContact_UserNotFound() {
        when(currentUser.getUsername()).thenReturn("currentUser");
        when(userService.getUserByUsername(anyString())).thenReturn(null);

        String viewName = contactController.addContact(currentUser, "connectedUser@example.com");

        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testAddContact_ConnectedUserNotFound() {
        User user = new User();
        user.setUsername("currentUser");

        when(currentUser.getUsername()).thenReturn("currentUser");
        when(userService.getUserByUsername(anyString())).thenReturn(user);
        when(userService.getUserByEmail(anyString())).thenReturn(null);

        String viewName = contactController.addContact(currentUser, "connectedUser@example.com");

        String expectedErrorMessage = URLEncoder.encode("Utilisateur à connecter invalide.", StandardCharsets.UTF_8);
        assertEquals("redirect:/contact?error=" + expectedErrorMessage, viewName);
    }
}
