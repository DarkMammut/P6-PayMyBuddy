package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetLoginPage() {
        String viewName = loginController.getLoginPage();
        assertEquals("login", viewName);
    }

    @Test
    public void testLogin_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        // Mocking the behavior of loginService to return an Optional.of(user) when the login is successful
        when(loginService.login("testuser", "password")).thenReturn(Optional.of(user));

        String viewName = loginController.login("testuser", "password");

        // Verifying that loginService.login() was called once with the correct parameters
        verify(loginService, times(1)).login("testuser", "password");
        assertEquals("redirect:/transfer", viewName);
    }

    @Test
    public void testLogin_Failure() {
        // Mocking the behavior of loginService to throw an exception when the login fails
        doThrow(new RuntimeException("Invalid username or password")).when(loginService).login(anyString(), anyString());

        String viewName = loginController.login("testuser", "wrongpassword");

        // Verifying that loginService.login() was called once with the correct parameters
        verify(loginService, times(1)).login("testuser", "wrongpassword");
        assertEquals("redirect:/login?error=Invalid+username+or+password", viewName);
    }
}