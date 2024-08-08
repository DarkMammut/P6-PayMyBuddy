package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.model.UserConnection;
import com.paymybuddy.moneytransfer.repository.UserConnectionRepository;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import com.paymybuddy.moneytransfer.service.exception.InvalidEmailException;
import com.paymybuddy.moneytransfer.service.exception.InvalidUsernameException;
import com.paymybuddy.moneytransfer.service.exception.InvalidPasswordException;
import com.paymybuddy.moneytransfer.service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,20}$");

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserConnectionRepository userConnectionRepository;

    @Transactional
    public void saveUser(User user) {
        validateUser(user);
        logger.info("Saving user: {}", user.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public boolean usernameExists(String username) {
        logger.debug("Checking if username exists: {}", username);
        return userRepository.findByUsername(username) != null;
    }

    public boolean emailExists(String email) {
        logger.debug("Checking if email exists: {}", email);
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
                () -> new UserNotFoundException("User not found with email: " + email)
        );
    }

    @Transactional
    public void updateUserDetails(int userId, String newUsername, String newEmail, String newPassword) {
        logger.info("Updating user details for userId: {}", userId);
        User user = getUserByUserId(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        if (isInvalidEmail(newEmail)) {
            throw new InvalidEmailException("Invalid email: " + newEmail);
        }
        if (isInvalidUsername(newUsername)) {
            throw new InvalidUsernameException("Invalid username: " + newUsername);
        }

        user.setUsername(newUsername);
        user.setEmail(newEmail);

        if (newPassword != null && !newPassword.isEmpty()) {
            if (isInvalidPassword(newPassword)) {
                throw new InvalidPasswordException("Invalid password format");
            }
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        }

        userRepository.save(user);
    }

    public List<User> getUserConnections(User user) {
        logger.info("Fetching user connections for user: {}", user.getUsername());
        List<UserConnection> connections = userConnectionRepository.findByUser(user);
        return connections.stream()
                .map(UserConnection::getConnectedUser)
                .collect(Collectors.toList());
    }

    private void validateUser(User user) {
        if (isInvalidEmail(user.getEmail())) {
            throw new InvalidEmailException("Invalid email: " + user.getEmail());
        }
        if (isInvalidUsername(user.getUsername())) {
            throw new InvalidUsernameException("Invalid username: " + user.getUsername());
        }
        if (isInvalidPassword(user.getPassword())) {
            throw new InvalidPasswordException("Invalid password format");
        }
    }

    private boolean isInvalidEmail(String email) {
        return email == null || !EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isInvalidUsername(String username) {
        return username == null || !USERNAME_PATTERN.matcher(username).matches();
    }

    private boolean isInvalidPassword(String password) {
        return password == null || !PASSWORD_PATTERN.matcher(password).matches();
    }
}