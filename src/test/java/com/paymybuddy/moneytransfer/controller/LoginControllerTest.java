package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

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

        when(loginService.login(anyString(), anyString())).thenReturn(Optional.of(user));

        String viewName = loginController.login("testuser", "password", request, model);

        verify(loginService, times(1)).login("testuser", "password");
        assertEquals("redirect:/transfer", viewName);
    }

    @Test
    public void testLogin_Failure() {
        when(loginService.login(anyString(), anyString())).thenReturn(Optional.empty());

        String viewName = loginController.login("testuser", "wrongpassword", request, model);

        verify(loginService, times(1)).login("testuser", "wrongpassword");
        verify(model, times(1)).addAttribute(eq("error"), eq("Username ou mot de passe invalide"));
        assertEquals("redirect:/login?error", viewName);
    }
}
