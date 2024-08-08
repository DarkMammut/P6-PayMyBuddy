package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.model.UserConnection;
import com.paymybuddy.moneytransfer.service.UserConnectionService;
import com.paymybuddy.moneytransfer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/contact")
public class ContactController {
    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    private final UserService userService;
    private final UserConnectionService userConnectionService;

    public ContactController(UserService userService, UserConnectionService userConnectionService) {
        this.userService = userService;
        this.userConnectionService = userConnectionService;
    }

    @GetMapping
    public String showAddContactForm(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        logger.info("Displaying add contact form for user: {}", currentUser.getUsername());

        User user = userService.getUserByUsername(currentUser.getUsername());
        if (user == null) {
            logger.error("User not found: {}", currentUser.getUsername());
            return "redirect:/login";
        }

        model.addAttribute("userConnection", new UserConnection());
        return "contact";
    }

    @PostMapping
    public String addContact(@AuthenticationPrincipal UserDetails currentUser, @RequestParam String connectedEmail) {
        logger.info("Adding contact for user: {}", currentUser.getUsername());

        User user = userService.getUserByUsername(currentUser.getUsername());
        User connectedUser = userService.getUserByEmail(connectedEmail);

        if (user == null) {
            logger.error("Current user not found: {}", currentUser.getUsername());
            return "redirect:/login";
        }

        if (connectedUser == null || user.equals(connectedUser)) {
            logger.error("Invalid connected user: {}", connectedEmail);
            String errorMessage = URLEncoder.encode("Utilisateur à connecter invalide.", StandardCharsets.UTF_8);
            return "redirect:/contact?error=" + errorMessage;
        }

        try {
            userConnectionService.addConnection(user.getUsername(), connectedUser.getEmail());
            logger.info("Successfully added connection between {} and {}", user.getUsername(), connectedUser.getEmail());
            return "redirect:/contact?success";
        } catch (Exception e) {
            logger.error("Error adding connection between {} and {}", user.getUsername(), connectedUser.getEmail(), e);
            String errorMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/contact?error=" + errorMessage;
        }
    }
}
