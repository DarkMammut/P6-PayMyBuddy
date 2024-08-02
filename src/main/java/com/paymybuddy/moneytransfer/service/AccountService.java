package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.Account;
import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    public Account getAccountByUserId(User userId) {
        logger.info("Fetching account for user with ID: {}", userId.getUserID());
        Account account = accountRepository.findByUserID(userId);
        if (account != null) {
            logger.info("Account found: {}", account);
        } else {
            logger.warn("No account found for user with ID: {}", userId.getUserID());
        }
        return account;
    }

    public void createAccount(User userId) {
        logger.info("Creating account for user with ID: {}", userId.getUserID());
        Account existingAccount = accountRepository.findByUserID(userId);
        if (existingAccount != null) {
            logger.error("User with ID: {} already has an account", userId.getUserID());
            throw new IllegalArgumentException("User already has an account");
        }

        Account newAccount = new Account();
        newAccount.setUserID(userId);
        newAccount.setBalance(BigDecimal.ZERO);

        accountRepository.save(newAccount);
        logger.info("Account successfully created for user with ID: {}", userId.getUserID());
    }

    public void changeBalance(User userId, BigDecimal newBalance) {
        logger.info("Changing balance for user with ID: {}. Amount: {}", userId.getUserID(), newBalance);
        Account currentAccount = accountRepository.findByUserID(userId);
        if (currentAccount == null) {
            logger.error("No account found for user with ID: {}", userId.getUserID());
            throw new IllegalArgumentException("Account not found");
        }
        currentAccount.setBalance(currentAccount.getBalance().add(newBalance));
        accountRepository.save(currentAccount);
        logger.info("Balance successfully updated for user with ID: {}. New Balance: {}", userId.getUserID(), currentAccount.getBalance());
    }
}