package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.model.UserConnection;
import com.paymybuddy.moneytransfer.repository.UserConnectionRepository;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserConnectionService {
    private final UserConnectionRepository userConnectionRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserConnectionService.class);

    public void addConnection(String username, String mail) {
        logger.info("Adding connection for username: {} with email: {}", username, mail);
        User currentUser = userRepository.findByUsername(username);
        Optional<User> connectedUser = userRepository.findByEmail(mail);

        if (connectedUser.isPresent()) {
            UserConnection userConnection = new UserConnection();
            userConnection.setConnectedUser(connectedUser.get());
            userConnection.setUser(currentUser);
            userConnectionRepository.save(userConnection);
            logger.info("Connection added successfully between {} and {}", username, mail);
        } else {
            logger.error("Email not found: {}", mail);
            throw new RuntimeException("Email not found");
        }
    }

    public boolean connectionExists(String username, String email) {
        logger.info("Checking if connection exists for username: {} with email: {}", username, email);
        User currentUser = userRepository.findByUsername(username);
        Optional<User> connectedUserOptional = userRepository.findByEmail(email);

        if (currentUser != null && connectedUserOptional.isPresent()) {
            User connectedUser = connectedUserOptional.get();
            boolean exists = userConnectionRepository.existsByUserAndConnectedUser(currentUser, connectedUser);
            logger.info("Connection exists: {}", exists);
            return exists;
        }

        logger.error("Connection does not exist because user or connected user is null");
        return false;  // Si l'un des utilisateurs n'existe pas, la connexion ne peut pas exister
    }
}