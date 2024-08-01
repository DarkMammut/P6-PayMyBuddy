package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.model.UserConnection;
import com.paymybuddy.moneytransfer.service.UserConnectionService;
import com.paymybuddy.moneytransfer.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        logger.info("Affichage du formulaire d'ajout de contact");

        User user = userService.getUserByUsername(currentUser.getUsername());
        if (user == null) {
            logger.warn("Utilisateur introuvable : {}", currentUser.getUsername());
            return "redirect:/login";
        }

        model.addAttribute("userConnection", new UserConnection());
        return "contact";
    }

    @PostMapping
    public String addContact(@AuthenticationPrincipal UserDetails currentUser, @RequestParam String connectedEmail) {
        logger.info("Demande d'ajout de contact pour l'utilisateur : {}", currentUser.getUsername());

        User user = userService.getUserByUsername(currentUser.getUsername());
        User connectedUser = userService.getUserByEmail(connectedEmail);

        if (user == null) {
            logger.error("Utilisateur actuel introuvable : {}", currentUser.getUsername());
            return "/contact";
        }

        if (connectedUser == null) {
            logger.error("Utilisateur à connecter est introuvable : {}", connectedEmail);
            return "/contact";
        }

        if (user == connectedUser) {
            logger.error("Utilisateur à connecter doit être différent de l'utilisateur actuel : {}", currentUser.getUsername());
            return "/contact";
        }

        if (!userConnectionService.connectionExists(currentUser.getUsername(), connectedEmail)) {
            logger.info("Ajout d'une nouvelle connexion entre {} et {}", user.getUsername(), connectedUser.getEmail());
            userConnectionService.addConnection(user.getUsername(), connectedUser.getEmail());
        } else {
            logger.info("La connexion existe déjà entre {} et {}", user.getUsername(), connectedUser.getEmail());
        }

        return "redirect:/contact";
    }
}
