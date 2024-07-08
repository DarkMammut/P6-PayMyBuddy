package com.paymybuddy.moneytransfer.repository;

import com.paymybuddy.moneytransfer.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
