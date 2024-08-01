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
            logger.warn("Username '{}' already exists", user.getUsername());
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        if (userService.emailExists(user.getEmail())) {
            logger.warn("Email '{}' already exists", user.getEmail());
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        // Save the user and create an account if everything is fine
        userService.saveUser(user);
        accountService.createAccount(user);
        logger.info("User '{}' registered successfully", user.getUsername());
        return "redirect:/login";
    }
}
