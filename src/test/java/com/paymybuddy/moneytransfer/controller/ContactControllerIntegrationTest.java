package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.service.UserConnectionService;
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
public class ContactControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserConnectionService userConnectionService;

    @BeforeEach
    void setup() {
        Mockito.reset(userService, userConnectionService);
    }

    @Test
    public void testShowAddContactForm() throws Exception {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userService.getUserByUsername(username)).thenReturn(user);

        mockMvc.perform(get("/contact")
                        .with(SecurityMockMvcRequestPostProcessors.user(username))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Assurez-vous que le token CSRF est inclus)
                .andExpect(status().isOk())
                .andExpect(view().name("contact"));
    }

    @Test
    public void testAddContactSuccess() throws Exception {
        String currentUsername = "testuser";
        String connectedEmail = "connecteduser@example.com";

        User user = new User();
        user.setUsername(currentUsername);

        User connectedUser = new User();
        connectedUser.setEmail(connectedEmail);

        when(userService.getUserByUsername(currentUsername)).thenReturn(user);
        when(userService.getUserByEmail(connectedEmail)).thenReturn(connectedUser);

        // Simulez un traitement réussi
        doNothing().when(userConnectionService).addConnection(currentUsername, connectedEmail);

        mockMvc.perform(post("/contact")
                        .with(SecurityMockMvcRequestPostProcessors.user(currentUsername))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("connectedEmail", connectedEmail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact?success"));
    }

    @Test
    public void testAddContactUserNotFound() throws Exception {
        String connectedEmail = "connecteduser@example.com";

        User user = new User();
        user.setUsername("testuser");

        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(userService.getUserByEmail(connectedEmail)).thenReturn(null);

        mockMvc.perform(post("/contact")
                        .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername()))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Assurez-vous que le token CSRF est inclus
                        .param("connectedEmail", connectedEmail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact?error=" + URLEncoder.encode("Utilisateur à connecter invalide.", StandardCharsets.UTF_8)));
    }

    @Test
    public void testAddContactSelfConnection() throws Exception {
        String connectedEmail = "testuser@example.com";

        User user = new User();
        user.setUsername("testuser");
        user.setEmail(connectedEmail);

        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(userService.getUserByEmail(connectedEmail)).thenReturn(user);

        mockMvc.perform(post("/contact")
                        .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername()))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Assurez-vous que le token CSRF est inclus
                        .param("connectedEmail", connectedEmail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact?error=" + URLEncoder.encode("Utilisateur à connecter invalide.", StandardCharsets.UTF_8)));
    }

    @Test
    public void testAddContactFailure() throws Exception {
        String connectedEmail = "connecteduser@example.com";

        User user = new User();
        user.setUsername("testuser");

        User connectedUser = new User();
        connectedUser.setEmail(connectedEmail);

        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(userService.getUserByEmail(connectedEmail)).thenReturn(connectedUser);
        doThrow(new RuntimeException("Failed to add connection")).when(userConnectionService).addConnection("testuser", connectedEmail);

        mockMvc.perform(post("/contact")
                        .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername()))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Assurez-vous que le token CSRF est inclus
                        .param("connectedEmail", connectedEmail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact?error=" + URLEncoder.encode("Failed to add connection", StandardCharsets.UTF_8)));
    }
}
