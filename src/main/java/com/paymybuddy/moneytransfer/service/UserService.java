package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.model.UserConnection;
import com.paymybuddy.moneytransfer.repository.UserConnectionRepository;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserConnectionRepository userConnectionRepository;

    public void saveUser(User user) {
        logger.info("Saving user: {}", user.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public boolean usernameExists(String username) {
        logger.info("Checking if username exists: {}", username);
        return userRepository.findByUsername(username) != null;
    }

    public boolean emailExists(String email) {
        logger.info("Checking if email exists: {}", email);
        return userRepository.findByEmail(email).isPresent();
    }

    public User getUserByUsername(String username) {
        logger.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    public User getUserByUserId(int userId) {
        logger.info("Fetching user by userId: {}", userId);
        return userRepository.findByUserID(userId);
    }

    public User getUserByEmail(String email) {
        logger.info("Fetching user by email: {}", email);
        return userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Email not found")
        );
    }

    public void updateUserDetails(int userId, String newUsername, String newEmail, String newPassword) {
        logger.info("Updating user details for userId: {}", userId);
        User user = getUserByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (newPassword == null) {
            throw new IllegalArgumentException("Empty password");
        }
        user.setUsername(newUsername);
        user.setEmail(newEmail);
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public List<User> getUserConnections(User user) {
        logger.info("Fetching user connections for user: {}", user.getUsername());
        List<UserConnection> connections = userConnectionRepository.findByUser(user);
        return connections.stream()
                .map(UserConnection::getConnectedUser)
                .collect(Collectors.toList());
    }
}