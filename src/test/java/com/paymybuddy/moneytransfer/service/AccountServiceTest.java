package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.Account;
import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAccountByUserId() {
        User user = new User();
        user.setUserID(1);

        Account account = new Account();
        account.setUserID(user);
        account.setBalance(BigDecimal.ZERO);

        when(accountRepository.findByUserID(user)).thenReturn(account);

        Account retrievedAccount = accountService.getAccountByUserId(user);

        assertNotNull(retrievedAccount);
        assertEquals(user, retrievedAccount.getUserID());
        verify(accountRepository, times(1)).findByUserID(user);
    }

    @Test
    public void testCreateAccount() {
        User user = new User();
        user.setUserID(1);

        when(accountRepository.findByUserID(user)).thenReturn(null);

        accountService.createAccount(user);

        verify(accountRepository, times(1)).findByUserID(user);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void testCreateAccountUserAlreadyHasAccount() {
        User user = new User();
        user.setUserID(1);

        Account existingAccount = new Account();
        existingAccount.setUserID(user);
        existingAccount.setBalance(BigDecimal.TEN);

        when(accountRepository.findByUserID(user)).thenReturn(existingAccount);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(user);
        });

        assertEquals("User already has an account", exception.getMessage());
        verify(accountRepository, times(1)).findByUserID(user);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void testChangeBalance() {
        User user = new User();
        user.setUserID(1);

        Account existingAccount = new Account();
        existingAccount.setUserID(user);
        existingAccount.setBalance(BigDecimal.TEN);

        when(accountRepository.findByUserID(user)).thenReturn(existingAccount);

        BigDecimal newBalance = BigDecimal.valueOf(15);

        accountService.changeBalance(user, newBalance);

        assertEquals(BigDecimal.valueOf(25), existingAccount.getBalance());
        verify(accountRepository, times(1)).findByUserID(user);
        verify(accountRepository, times(1)).save(existingAccount);
    }
}