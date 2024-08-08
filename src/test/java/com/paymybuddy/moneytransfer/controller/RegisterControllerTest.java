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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class RegisterControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private RegisterController registerController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRegisterPage() {
        String viewName = registerController.getRegisterPage(model);
        verify(model).addAttribute("user", new User());
        assertEquals("register", viewName);
    }

    @Test
    public void testRegisterUser_Success() {
        User user = new User();
        String viewName = registerController.registerUser(user, model);
        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testRegisterUser_Error() throws Exception {
        User user = new User();
        doThrow(new RuntimeException("Cette adresse mail est déjà utilisée")).when(userService).saveUser(user);

        String viewName = registerController.registerUser(user, model);

        String expectedErrorMessage = URLEncoder.encode("Cette adresse mail est déjà utilisée", StandardCharsets.UTF_8.toString());
        assertEquals("redirect:/register?error=" + expectedErrorMessage, viewName);
    }
}