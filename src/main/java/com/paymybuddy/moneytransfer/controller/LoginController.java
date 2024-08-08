package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
    public String login(@RequestParam String username, @RequestParam String password) {
        logger.info("Attempting login for user: {}", username);
        try {
            loginService.login(username, password);
            logger.info("Login successful for user: {}", username);
            return "redirect:/transfer";
        } catch (Exception e) {
            logger.error("Erreur lors de la connexion de l'utilisateur {} : {}", username, e.getMessage());
            String errorMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/login?error=" + errorMessage;
        }
    }
}