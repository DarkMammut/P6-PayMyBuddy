package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.Account;
import com.paymybuddy.moneytransfer.model.Transaction;
import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.service.AccountService;
import com.paymybuddy.moneytransfer.service.TransactionService;
import com.paymybuddy.moneytransfer.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TransferControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserDetails currentUser;

    @Mock
    private Model model;

    @InjectMocks
    private TransferController transferController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowTransferForm_UserNotFound() {
        when(currentUser.getUsername()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(null);

        String viewName = transferController.showTransferForm(currentUser, model);

        verify(userService, times(1)).getUserByUsername("testUser");
        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testShowTransferForm_AccountNotFound() {
        User user = new User();
        user.setUsername("testUser");

        when(currentUser.getUsername()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(user);
        when(accountService.getAccountByUserId(user)).thenReturn(null);

        String viewName = transferController.showTransferForm(currentUser, model);

        verify(userService, times(1)).getUserByUsername("testUser");
        verify(accountService, times(1)).getAccountByUserId(user);
        assertEquals("transfer", viewName);
    }

    @Test
    public void testShowTransferForm_Success() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("testUser");
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(1000));
        List<Transaction> transactions = new ArrayList<>();
        List<User> userConnections = new ArrayList<>();

        when(currentUser.getUsername()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(user);
        when(accountService.getAccountByUserId(user)).thenReturn(account);
        when(transactionService.getTransactionsByAccount(account)).thenReturn(transactions);
        when(userService.getUserConnections(user)).thenReturn(userConnections);

        String viewName = transferController.showTransferForm(currentUser, model);

        verify(userService, times(1)).getUserByUsername("testUser");
        verify(accountService, times(1)).getAccountByUserId(user);
        verify(transactionService, times(1)).getTransactionsByAccount(account);
        verify(userService, times(1)).getUserConnections(user);
        verify(model, times(1)).addAttribute("transactions", transactions);
        verify(model, times(1)).addAttribute("userConnections", userConnections);
        verify(model, times(1)).addAttribute("balance", account.getBalance());
        assertEquals("transfer", viewName);
    }

    @Test
    public void testProcessTransfer_UserNotFound() {
        when(currentUser.getUsername()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(null);

        String viewName = transferController.processTransfer(currentUser, "receiverUser", "Test description", BigDecimal.valueOf(100));

        verify(userService, times(1)).getUserByUsername("testUser");
        assertEquals("redirect:/transfer?error=Destinataire+invalide", viewName);
    }

    @Test
    public void testProcessTransfer_ReceiverNotFound() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("testUser");

        when(currentUser.getUsername()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(user);
        when(userService.getUserByUsername("receiverUser")).thenReturn(null);

        String viewName = transferController.processTransfer(currentUser, "receiverUser", "Test description", BigDecimal.valueOf(100));

        verify(userService, times(1)).getUserByUsername("receiverUser");
        assertEquals("redirect:/transfer?error=Destinataire+invalide", viewName);
    }

    @Test
    public void testProcessTransfer_SameUser() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("testUser");

        when(currentUser.getUsername()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(user);
        when(userService.getUserByUsername("testUser")).thenReturn(user);

        String viewName = transferController.processTransfer(currentUser, "testUser", "Test description", BigDecimal.valueOf(100));

        assertEquals("redirect:/transfer?error=Destinataire+invalide", viewName);
    }

    @Test
    public void testProcessTransfer_Success() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("testUser");
        User receiver = new User();
        receiver.setUsername("receiverUser");

        when(currentUser.getUsername()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(user);
        when(userService.getUserByUsername("receiverUser")).thenReturn(receiver);

        doNothing().when(transactionService).processTransaction(user.getUserID(), "receiverUser", BigDecimal.valueOf(100), "Test description");

        String viewName = transferController.processTransfer(currentUser, "receiverUser", "Test description", BigDecimal.valueOf(100));

        verify(transactionService, times(1)).processTransaction(user.getUserID(), "receiverUser", BigDecimal.valueOf(100), "Test description");
        assertEquals("redirect:/transfer?success", viewName);
    }

    @Test
    public void testProcessTransfer_Exception() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("testUser");
        User receiver = new User();
        receiver.setUsername("receiverUser");

        when(currentUser.getUsername()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(user);
        when(userService.getUserByUsername("receiverUser")).thenReturn(receiver);

        doThrow(new RuntimeException("Transaction failed")).when(transactionService).processTransaction(user.getUserID(), "receiverUser", BigDecimal.valueOf(100), "Test description");

        String viewName = transferController.processTransfer(currentUser, "receiverUser", "Test description", BigDecimal.valueOf(100));

        verify(transactionService, times(1)).processTransaction(user.getUserID(), "receiverUser", BigDecimal.valueOf(100), "Test description");
        assertEquals("redirect:/transfer?error=Transaction+failed", viewName);
    }

    @Test
    public void testProccessAddMoney_UserNotFound() {
        when(currentUser.getUsername()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(null);

        String viewName = transferController.proccessAddMoney(currentUser, BigDecimal.valueOf(100));

        verify(userService, times(1)).getUserByUsername("testUser");
        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testProccessAddMoney_Success() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("testUser");

        when(currentUser.getUsername()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(user);

        doNothing().when(accountService).changeBalance(user, BigDecimal.valueOf(100));

        String viewName = transferController.proccessAddMoney(currentUser, BigDecimal.valueOf(100));

        verify(accountService, times(1)).changeBalance(user, BigDecimal.valueOf(100));
        assertEquals("redirect:/transfer?addsuccess", viewName);
    }

    @Test
    public void testProccessAddMoney_Exception() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("testUser");

        when(currentUser.getUsername()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(user);

        doThrow(new RuntimeException("Failed to add money")).when(accountService).changeBalance(user, BigDecimal.valueOf(100));

        String viewName = transferController.proccessAddMoney(currentUser, BigDecimal.valueOf(100));

        verify(accountService, times(1)).changeBalance(user, BigDecimal.valueOf(100));
        assertEquals("redirect:/transfer?adderror=Failed+to+add+money", viewName);
    }
}
