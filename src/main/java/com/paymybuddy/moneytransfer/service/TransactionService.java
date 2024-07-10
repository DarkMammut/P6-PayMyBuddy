package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.Account;
import com.paymybuddy.moneytransfer.model.Transaction;
import com.paymybuddy.moneytransfer.repository.AccountRepository;
import com.paymybuddy.moneytransfer.repository.TransactionRepository;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        Account account = findAccountByUserID(transaction.getSender());
        if (account.getBalance() == null) {}
    }
}
