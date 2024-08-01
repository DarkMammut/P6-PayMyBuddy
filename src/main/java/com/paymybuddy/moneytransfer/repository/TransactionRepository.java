package com.paymybuddy.moneytransfer.repository;

import com.paymybuddy.moneytransfer.model.Account;
import com.paymybuddy.moneytransfer.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByAccount(Account accountID);
}
