package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProfileControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Model model;

    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowEditFormUserFound() {
        User user = new User();
        user.setUsername("testuser");

        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(user);

        String viewName = profileController.showEditForm(userDetails, model);

        verify(model, times(1)).addAttribute("user", user);
        assertEquals("profile", viewName);
    }

    @Test
    public void testShowEditFormUserNotFound() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(null);

        String viewName = profileController.showEditForm(userDetails, model);

        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testUpdateAccountUserFound() {
        User existingUser = new User();
        existingUser.setUserID(1);
        existingUser.setUsername("existingUser");

        User updatedUser = new User();
        updatedUser.setUsername("updatedUser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("newpassword");

        when(userDetails.getUsername()).thenReturn("existingUser");
        when(userService.getUserByUsername("existingUser")).thenReturn(existingUser);

        String viewName = profileController.updateAccount(userDetails, updatedUser, model);

        verify(userService, times(1)).updateUserDetails(1, "updatedUser", "updated@example.com", "newpassword");
        assertEquals("redirect:/profile?success", viewName);
    }

    @Test
    public void testUpdateAccountUserNotFound() {
        User updatedUser = new User();
        updatedUser.setUsername("updatedUser");

        when(userDetails.getUsername()).thenReturn("nonexistentUser");
        when(userService.getUserByUsername("nonexistentUser")).thenReturn(null);

        String viewName = profileController.updateAccount(userDetails, updatedUser, model);

        assertEquals("redirect:/profile?error=Utilisateur+introuvable", viewName);
    }
}