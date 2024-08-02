package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.Account;
import com.paymybuddy.moneytransfer.model.Transaction;
import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.repository.AccountRepository;
import com.paymybuddy.moneytransfer.repository.TransactionRepository;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTransactionsByAccount() {
        Account account = new Account();
        account.setAccountID(1);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        List<Transaction> transactions = Collections.singletonList(transaction);

        when(transactionRepository.findByAccount(account)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsByAccount(account);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(transaction, result.get(0));
        verify(transactionRepository, times(1)).findByAccount(account);
    }

    @Test
    public void testProcessTransactionSuccessful() {
        User sender = new User();
        sender.setUserID(1);
        sender.setUsername("sender");

        User receiver = new User();
        receiver.setUserID(2);
        receiver.setUsername("receiver");

        Account senderAccount = new Account();
        senderAccount.setUserID(sender);
        senderAccount.setBalance(new BigDecimal("1000"));

        Account receiverAccount = new Account();
        receiverAccount.setUserID(receiver);
        receiverAccount.setBalance(new BigDecimal("500"));

        when(userRepository.findByUserID(1)).thenReturn(sender);
        when(userRepository.findByUsername("receiver")).thenReturn(receiver);
        when(accountRepository.findByUserID(sender)).thenReturn(senderAccount);
        when(accountRepository.findByUserID(receiver)).thenReturn(receiverAccount);

        transactionService.processTransaction(1, "receiver", new BigDecimal("100"), "Test Transaction");

        assertEquals(new BigDecimal("899.50").setScale(2, RoundingMode.HALF_EVEN),
                senderAccount.getBalance().setScale(2, RoundingMode.HALF_EVEN));
        assertEquals(new BigDecimal("600").setScale(2, RoundingMode.HALF_EVEN),
                receiverAccount.getBalance().setScale(2, RoundingMode.HALF_EVEN));
        verify(accountRepository, times(1)).save(senderAccount);
        verify(accountRepository, times(1)).save(receiverAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testProcessTransactionInsufficientBalance() {
        User sender = new User();
        sender.setUserID(1);
        sender.setUsername("sender");

        User receiver = new User();
        receiver.setUserID(2);
        receiver.setUsername("receiver");

        Account senderAccount = new Account();
        senderAccount.setUserID(sender);
        senderAccount.setBalance(new BigDecimal("50"));

        Account receiverAccount = new Account();
        receiverAccount.setUserID(receiver);
        receiverAccount.setBalance(new BigDecimal("500"));

        when(userRepository.findByUserID(1)).thenReturn(sender);
        when(userRepository.findByUsername("receiver")).thenReturn(receiver);
        when(accountRepository.findByUserID(sender)).thenReturn(senderAccount);
        when(accountRepository.findByUserID(receiver)).thenReturn(receiverAccount);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.processTransaction(1, "receiver", new BigDecimal("100"), "Test Transaction");
        });

        assertEquals("Insufficient balance. Transaction aborted.", exception.getMessage());
        verify(accountRepository, never()).save(senderAccount);
        verify(accountRepository, never()).save(receiverAccount);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testProcessTransactionSenderNotFound() {
        when(userRepository.findByUserID(1)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.processTransaction(1, "receiver", new BigDecimal("100"), "Test Transaction");
        });

        assertEquals("Sender not found. Transaction aborted.", exception.getMessage());
        verify(userRepository, times(1)).findByUserID(1);
        verify(accountRepository, never()).findByUserID(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testProcessTransactionReceiverNotFound() {
        User sender = new User();
        sender.setUserID(1);
        sender.setUsername("sender");

        when(userRepository.findByUserID(1)).thenReturn(sender);
        when(userRepository.findByUsername("receiver")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.processTransaction(1, "receiver", new BigDecimal("100"), "Test Transaction");
        });

        assertEquals("Receiver not found. Transaction aborted.", exception.getMessage());
        verify(userRepository, times(1)).findByUserID(1);
        verify(userRepository, times(1)).findByUsername("receiver");
        verify(accountRepository, never()).findByUserID(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}