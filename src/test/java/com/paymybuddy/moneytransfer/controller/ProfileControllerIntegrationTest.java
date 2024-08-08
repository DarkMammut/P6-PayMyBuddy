package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setup() {
        Mockito.reset(userService);
    }

    @Test
    public void testShowProfileForm() throws Exception {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userService.getUserByUsername(username)).thenReturn(user);

        mockMvc.perform(get("/profile")
                        .with(SecurityMockMvcRequestPostProcessors.user(username))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Assurez-vous que le token CSRF est inclus)
                .andExpect(status().isOk())
                .andExpect(view().name("profile"));
    }

    @Test
    public void testUpdateProfileSuccess() throws Exception {
        String username = "testuser";
        String newEmail = "newemail@example.com";
        String newPassword = "newpassword";

        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setUserID(1); // Utilisez un ID valide pour le test

        User updatedUser = new User();
        updatedUser.setUsername(username);
        updatedUser.setEmail(newEmail);
        updatedUser.setPassword(newPassword);

        when(userService.getUserByUsername(username)).thenReturn(existingUser);
        doNothing().when(userService).updateUserDetails(existingUser.getUserID(), username, newEmail, newPassword);

        mockMvc.perform(post("/profile")
                        .with(SecurityMockMvcRequestPostProcessors.user(username))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("username", username)
                        .param("email", newEmail)
                        .param("password", newPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success"));
    }

    @Test
    public void testUpdateProfileUserNotFound() throws Exception {
        String username = "testuser";
        String newEmail = "newemail@example.com";
        String newPassword = "newpassword";

        when(userService.getUserByUsername(username)).thenReturn(null);

        mockMvc.perform(post("/profile")
                        .with(SecurityMockMvcRequestPostProcessors.user(username))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("username", username)
                        .param("email", newEmail)
                        .param("password", newPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?error=" + URLEncoder.encode("Utilisateur introuvable", StandardCharsets.UTF_8)));
    }

    @Test
    public void testUpdateProfileFailure() throws Exception {
        String username = "testuser";
        String newEmail = "newemail@example.com";
        String newPassword = "newpassword";

        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setUserID(1); // Utilisez un ID valide pour le test

        when(userService.getUserByUsername(username)).thenReturn(existingUser);
        doThrow(new RuntimeException("Erreur lors de la mise à jour")).when(userService).updateUserDetails(existingUser.getUserID(), username, newEmail, newPassword);

        mockMvc.perform(post("/profile")
                        .with(SecurityMockMvcRequestPostProcessors.user(username))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("username", username)
                        .param("email", newEmail)
                        .param("password", newPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?error=" + URLEncoder.encode("Erreur lors de la mise à jour", StandardCharsets.UTF_8)));
    }
}
