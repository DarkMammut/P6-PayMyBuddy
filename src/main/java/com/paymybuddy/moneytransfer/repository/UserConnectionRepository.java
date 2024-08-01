package com.paymybuddy.moneytransfer.repository;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.model.UserConnection;
import com.paymybuddy.moneytransfer.model.UserConnectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnection, UserConnectionId> {
    List<UserConnection> findByUser(User user);
    boolean existsByUserAndConnectedUser(User user, User connectedUser);

}
