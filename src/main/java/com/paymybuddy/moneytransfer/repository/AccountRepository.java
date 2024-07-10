package com.paymybuddy.moneytransfer.repository;

import com.paymybuddy.moneytransfer.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByUserID(Integer userID);
}
