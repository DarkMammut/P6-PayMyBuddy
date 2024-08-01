package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.controller.LoginController;
import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> login(String username, String password) {
        logger.info("Attempting to login user with username: {}", username);
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.info("User found for username: {}", username);

            if (passwordEncoder.matches(password, user.getPassword())) {
                logger.info("Password match for username: {}", username);
                return userOptional;
            } else {
                logger.warn("Password mismatch for username: {}", username);
            }
        } else {
            logger.warn("No user found for username: {}", username);
        }

        return Optional.empty();
    }
}
