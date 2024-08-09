package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.model.UserConnection;
import com.paymybuddy.moneytransfer.repository.UserConnectionRepository;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserConnectionService {
    private final UserConnectionRepository userConnectionRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserConnectionService.class);

    @Transactional
    public void addConnection(String username, String email) {
        logger.info("Adding connection for username: {} with email: {}", username, email);

        if (username == null || email == null) {
            logger.error("Username or email cannot be null");
            throw new IllegalArgumentException("Username and email cannot be null");
        }

        User currentUser = userRepository.findByUsername(username);
        Optional<User> connectedUser = userRepository.findByEmail(email);

        if (currentUser == null) {
            logger.error("User not found: {}", username);
            throw new RuntimeException("Utilisateur non trouvé");
        }

        if (connectedUser.isPresent()) {
            if (!connectionExists(username, email)) {
                logger.info("Adding new connection between {} and {}", username, email);
                UserConnection userConnection = new UserConnection();
                userConnection.setConnectedUser(connectedUser.get());
                userConnection.setUser(currentUser);
                userConnectionRepository.save(userConnection);
                logger.info("Connection added successfully between {} and {}", username, email);
            } else {
                logger.error("Connection already exists between {} and {}", username, email);
                throw new RuntimeException("L'utilisateur fait déjà partie de vos connexions");
            }
        } else {
            logger.error("Email not found: {}", email);
            throw new RuntimeException("Utilisateur invalide");
        }
    }

    public boolean connectionExists(String username, String email) {
        logger.info("Checking if connection exists for username: {} with email: {}", username, email);

        if (username == null || email == null) {
            logger.error("Username or email cannot be null");
            throw new IllegalArgumentException("Username or email cannot be null");
        }

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
