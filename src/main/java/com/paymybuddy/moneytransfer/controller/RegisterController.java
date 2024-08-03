package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.service.AccountService;
import com.paymybuddy.moneytransfer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final AccountService accountService;
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        logger.info("Get register page");
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        logger.info("POST request received for /register");

        // Check for existing username and email
        if (userService.usernameExists(user.getUsername())) {
            logger.error("Username '{}' already exists", user.getUsername());
            model.addAttribute("error", "Username déjà utilisé");
            return "redirect:/register?error";
        }

        if (userService.emailExists(user.getEmail())) {
            logger.error("Email '{}' already exists", user.getEmail());
            model.addAttribute("error", "Un compte existe déjà avec cette adresse email");
            return "redirect:/register?error";
        }

        // Save the user and create an account if everything is fine
        try {
            userService.saveUser(user);
            accountService.createAccount(user);
            logger.info("User '{}' registered successfully", user.getUsername());
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/register?error";
        }
    }
}
