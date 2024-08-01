package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
@RequestMapping("/login")
public class LoginController {

    Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping
    public String getLoginPage() {
        logger.info("Get Login Page");
        return "login";
    }

    @PostMapping
    public String login(@RequestParam String username, @RequestParam String password, HttpServletRequest request, Model model) {
        logger.info("Attempting login for user: {}", username);
        Optional<User> userOptional = loginService.login(username, password);
        if (userOptional.isPresent()) {
            logger.info("Login successful for user: {}", username);
            return "redirect:/transfer";
        } else {
            logger.warn("Login failed for user: {}", username);
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
}