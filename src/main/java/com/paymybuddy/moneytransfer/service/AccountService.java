package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.Account;
import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account getAccountByUserId(User userId) {
        return accountRepository.findByUserID(userId);
    }

    public void createAccount(User userId) {
        Account existingAccount = accountRepository.findByUserID(userId);
        if (existingAccount != null) {
            throw new IllegalArgumentException("User already has an account");
        }

        Account newAccount = new Account();
        newAccount.setUserID(userId);
        newAccount.setBalance(BigDecimal.ZERO);

        accountRepository.save(newAccount);
    }

    public void changeBalance(User userId, BigDecimal newBalance) {
        Account currentAccount = accountRepository.findByUserID(userId);
        currentAccount.setBalance(currentAccount.getBalance().add(newBalance));
        accountRepository.save(currentAccount);
    }
}