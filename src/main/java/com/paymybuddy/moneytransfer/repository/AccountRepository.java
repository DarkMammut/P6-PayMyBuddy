package com.paymybuddy.moneytransfer.repository;

import com.paymybuddy.moneytransfer.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Query("select a from Account a where a.userID.userID = ?1")
    Account findByUserId(Integer userID);
}
