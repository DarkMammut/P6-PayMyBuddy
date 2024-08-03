package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.service.AccountService;
import com.paymybuddy.moneytransfer.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RegisterControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AccountService accountService;

    @Mock
    private Model model;

    @InjectMocks
    private RegisterController registerController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRegisterPage() {
        String viewName = registerController.getRegisterPage(model);

        verify(model, times(1)).addAttribute(eq("user"), any(User.class));
        assertEquals("register", viewName);
    }

    @Test
    public void testRegisterUserUsernameExists() {
        User user = new User();
        user.setUsername("existingUser");
        user.setEmail("newemail@example.com");

        when(userService.usernameExists("existingUser")).thenReturn(true);

        String viewName = registerController.registerUser(user, model);

        verify(userService, times(1)).usernameExists("existingUser");
        verify(model, times(1)).addAttribute("error", "Username déjà utilisé");
        assertEquals("redirect:/register?error", viewName);
    }

    @Test
    public void testRegisterUserEmailExists() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("existing@example.com");

        when(userService.usernameExists("newuser")).thenReturn(false);
        when(userService.emailExists("existing@example.com")).thenReturn(true);

        String viewName = registerController.registerUser(user, model);

        verify(userService, times(1)).usernameExists("newuser");
        verify(userService, times(1)).emailExists("existing@example.com");
        verify(model, times(1)).addAttribute("error", "Un compte existe déjà avec cette adresse email");
        assertEquals("redirect:/register?error", viewName);
    }

    @Test
    public void testRegisterUserSuccess() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");

        when(userService.usernameExists("newuser")).thenReturn(false);
        when(userService.emailExists("new@example.com")).thenReturn(false);

        String viewName = registerController.registerUser(user, model);

        verify(userService, times(1)).usernameExists("newuser");
        verify(userService, times(1)).emailExists("new@example.com");
        verify(userService, times(1)).saveUser(user);
        verify(accountService, times(1)).createAccount(user);
        assertEquals("redirect:/login", viewName);
    }
}