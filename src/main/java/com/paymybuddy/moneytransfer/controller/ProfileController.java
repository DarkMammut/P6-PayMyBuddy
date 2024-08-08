package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.User;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showEditForm(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        logger.info("Affichage du formulaire de modification du profil pour l'utilisateur : {}", currentUser.getUsername());

        User user = userService.getUserByUsername(currentUser.getUsername());
        if (user == null) {
            logger.error("Utilisateur introuvable : {}", currentUser.getUsername());
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping
    public String updateAccount(@AuthenticationPrincipal UserDetails currentUser, User user, Model model) {
        logger.info("Mise à jour du compte pour l'utilisateur : {}", currentUser.getUsername());
        User existingUser = userService.getUserByUsername(currentUser.getUsername());

        if (existingUser == null) {
            logger.error("Utilisateur introuvable : {}", currentUser.getUsername());
            String errorMessage = URLEncoder.encode("Utilisateur introuvable", StandardCharsets.UTF_8);
            return "redirect:/profile?error=" + errorMessage;
        }

        try {
            userService.updateUserDetails(existingUser.getUserID(), user.getUsername(), user.getEmail(), user.getPassword());
            logger.info("Compte mis à jour pour l'utilisateur : {}", existingUser.getUsername());
            return "redirect:/profile?success";
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur {} : {}", existingUser.getUsername(), e.getMessage());
            String errorMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/profile?error=" + errorMessage;
        }
    }
}