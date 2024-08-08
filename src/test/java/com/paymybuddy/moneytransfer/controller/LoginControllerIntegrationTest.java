//package com.paymybuddy.moneytransfer.controller;
//
//import com.paymybuddy.moneytransfer.model.User;
//import com.paymybuddy.moneytransfer.service.LoginService;
//import com.paymybuddy.moneytransfer.service.LoginService.PasswordMismatchException;
//import com.paymybuddy.moneytransfer.service.LoginService.UserNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.Optional;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class LoginControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private LoginService loginService;
//
//    @BeforeEach
//    void setup() {
//        Mockito.reset(loginService);
//    }
//
//    @Test
//    public void testLoginSuccess() throws Exception {
//        String username = "testuser";
//        String password = "password";
//        String encodedPassword = "encodedpassword";
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(encodedPassword); // Set the password here
//
//        when(loginService.login(username, password)).thenReturn(Optional.of(user));
//
//        mockMvc.perform(post("/login")
//                        .param("username", username)
//                        .param("password", password)
//                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/transfer"));
//    }
//
//    @Test
//    public void testLoginUserNotFound() throws Exception {
//        String username = "testuser";
//        String password = "password";
//
//        when(loginService.login(username, password)).thenThrow(new UserNotFoundException("No user found for username"));
//
//        mockMvc.perform(post("/login")
//                        .param("username", username)
//                        .param("password", password)
//                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/login?error=" + URLEncoder.encode("No user found for username", StandardCharsets.UTF_8)));
//    }
//
//    @Test
//    public void testLoginPasswordMismatch() throws Exception {
//        String username = "testuser";
//        String password = "password";
//
//        when(loginService.login(username, password)).thenThrow(new PasswordMismatchException("Password mismatch for username"));
//
//        mockMvc.perform(post("/login")
//                        .param("username", username)
//                        .param("password", password)
//                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Include CSRF token
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/login?error=" + URLEncoder.encode("Password mismatch for username", StandardCharsets.UTF_8)));
//    }
//}
