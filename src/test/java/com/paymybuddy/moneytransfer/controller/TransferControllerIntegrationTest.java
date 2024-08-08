package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.Account;
import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.service.AccountService;
import com.paymybuddy.moneytransfer.service.TransactionService;
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

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private UserService userService;

    @MockBean
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        Mockito.reset(accountService, userService, transactionService);
    }

    @Test
    public void testShowTransferForm() throws Exception {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(100.00));
        when(userService.getUserByUsername(username)).thenReturn(user);
        when(accountService.getAccountByUserId(user)).thenReturn(account);
        when(transactionService.getTransactionsByAccount(account)).thenReturn(Collections.emptyList());
        when(userService.getUserConnections(user)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/transfer")
                        .with(SecurityMockMvcRequestPostProcessors.user(username))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // Assurez-vous que le token CSRF est inclus)
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attribute("balance", BigDecimal.valueOf(100.00)));
    }

    @Test
    public void testProcessTransferSuccess() throws Exception {
        String senderUsername = "testuser";
        String receiverUsername = "receiver";
        BigDecimal amount = BigDecimal.valueOf(50.0);
        String description = "Payment";

        User sender = new User();
        sender.setUsername(senderUsername);
        sender.setUserID(1);  // Assurez-vous que l'ID n'est pas nul

        User receiver = new User();
        receiver.setUsername(receiverUsername);
        receiver.setUserID(2);  // Assurez-vous que l'ID n'est pas nul

        // Simulez l'utilisateur actuel et le récepteur
        when(userService.getUserByUsername(senderUsername)).thenReturn(sender);
        when(userService.getUserByUsername(receiverUsername)).thenReturn(receiver);

        // Simulez un traitement réussi
        doNothing().when(transactionService).processTransaction(anyInt(), anyString(), any(BigDecimal.class), anyString());

        mockMvc.perform(post("/transfer")
                        .with(SecurityMockMvcRequestPostProcessors.user(senderUsername))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("receiverUsername", receiverUsername)
                        .param("description", description)
                        .param("transferAmount", amount.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer?success"));
    }


    @Test
    public void testProcessTransferFailure() throws Exception {
        String username = "testuser";
        String receiverUsername = "invalidreceiver";
        BigDecimal amount = BigDecimal.valueOf(50.00);
        String description = "Payment";
        String errorMessage = "Destinataire invalide";

        when(userService.getUserByUsername(username)).thenReturn(new User());
        when(userService.getUserByUsername(receiverUsername)).thenReturn(null);

        mockMvc.perform(post("/transfer")
                        .with(SecurityMockMvcRequestPostProcessors.user(username))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Assurez-vous que le token CSRF est inclus
                        .param("receiverUsername", receiverUsername)
                        .param("description", description)
                        .param("transferAmount", amount.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8)));
    }

    @Test
    public void testProcessAddMoneySuccess() throws Exception {
        String username = "testuser";
        BigDecimal amount = BigDecimal.valueOf(50.00);
        User user = new User();
        user.setUsername(username);

        when(userService.getUserByUsername(username)).thenReturn(user);

        mockMvc.perform(post("/transfer/add")
                        .with(SecurityMockMvcRequestPostProcessors.user(username))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Assurez-vous que le token CSRF est inclus
                        .param("addAmount", amount.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer?addsuccess"));
    }

    @Test
    public void testProcessAddMoneyFailure() throws Exception {
        String username = "testuser";
        BigDecimal amount = BigDecimal.valueOf(50.00);

        User user = new User();
        user.setUsername(username);

        // Simulez l'utilisateur avec succès
        when(userService.getUserByUsername(username)).thenReturn(user);

        // Simulez une erreur dans le service de compte
        doThrow(new RuntimeException("Erreur lors du versement")).when(accountService).changeBalance(user, amount);

        mockMvc.perform(post("/transfer/add")
                        .with(SecurityMockMvcRequestPostProcessors.user(username))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("addAmount", amount.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer?adderror=" + URLEncoder.encode("Erreur lors du versement", StandardCharsets.UTF_8)));
    }

}
