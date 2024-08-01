package com.paymybuddy.moneytransfer.repository;

import com.paymybuddy.moneytransfer.model.Account;
import com.paymybuddy.moneytransfer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByUserID(User userID);
}
