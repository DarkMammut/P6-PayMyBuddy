package com.paymybuddy.moneytransfer.repository;

import com.paymybuddy.moneytransfer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    exemple requête @Query("select * from User.name")
    User findByUsername(String username);
}
