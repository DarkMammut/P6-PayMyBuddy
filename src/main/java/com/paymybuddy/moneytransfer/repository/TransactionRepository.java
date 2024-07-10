package com.paymybuddy.moneytransfer.repository;

import com.paymybuddy.moneytransfer.model.Transaction;
import com.paymybuddy.moneytransfer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
