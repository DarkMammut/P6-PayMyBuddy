package com.paymybuddy.moneytransfer.controller;

import com.paymybuddy.moneytransfer.model.Account;
import com.paymybuddy.moneytransfer.model.Transaction;
import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.service.AccountService;
import com.paymybuddy.moneytransfer.service.TransactionService;
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

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/transfer")
public class TransferController {

    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

    private final AccountService accountService;
    private final UserService userService;
    private final TransactionService transactionService;

    public TransferController(AccountService accountService, UserService userService, TransactionService transactionService) {
        this.accountService = accountService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public String showTransferForm(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        logger.info("Affichage du formulaire de transfert pour l'utilisateur : {}", currentUser.getUsername());

        User user = userService.getUserByUsername(currentUser.getUsername());
        if (user == null) {
            logger.error("Utilisateur introuvable : {}", currentUser.getUsername());
            return "redirect:/login";
        }

        Account currentAccount = accountService.getAccountByUserId(user);
        if (currentAccount == null) {
            logger.error("Compte introuvable pour l'utilisateur : {}", user.getUsername());
            return "transfer";
        }

        List<Transaction> transactions = transactionService.getTransactionsByAccount(currentAccount);
        List<User> userConnections = userService.getUserConnections(user);
        BigDecimal balance = currentAccount.getBalance();

        model.addAttribute("transactions", transactions);
        model.addAttribute("userConnections", userConnections);
        model.addAttribute("balance", balance);

        return "transfer";
    }

    @PostMapping
    public String processTransfer(@AuthenticationPrincipal UserDetails currentUser,
                                  @RequestParam("receiverUsername") String receiverUsername,
                                  @RequestParam("description") String description,
                                  @RequestParam("transferAmount") BigDecimal amount) {

        logger.info("Traitement d'un transfert de {} à {} pour un montant de {} avec la description '{}'",
                currentUser.getUsername(), receiverUsername, amount, description);

        User user = userService.getUserByUsername(currentUser.getUsername());
        User receiver = userService.getUserByUsername(receiverUsername);

        if (receiver == null) {
            logger.error("Destinataire introuvable : {}", receiverUsername);
            return "redirect:/transfer?error";
        }

        if (receiver == user) {
            logger.error("Destinataire doit être différent de l'utilisateur actuel : {}", receiverUsername);
            return "redirect:/transfer?error";
        }

        if (user == null) {
            logger.error("Utilisateur introuvable : {}", currentUser.getUsername());
            return "redirect:/login";
        }

        try {
            transactionService.processTransaction(user.getUserID(), receiverUsername, amount, description);
            logger.info("Transfert réussi de {} à {} pour un montant de {}", user.getUsername(), receiverUsername, amount);
            return "redirect:/transfer?success";
        } catch (Exception e) {
            logger.error("Erreur lors du traitement du transfert de {} à {} pour un montant de {}: {}",
                    user.getUsername(), receiverUsername, amount, e.getMessage());
            return "redirect:/transfer?error";
        }
    }

    @PostMapping("/add")
    public String proccessAddMoney(@AuthenticationPrincipal UserDetails currentUser, @RequestParam("addAmount") BigDecimal amount) {
        logger.info("Traitement d'un transfert de {} pour un montant de {}",
                currentUser.getUsername(), amount);
        User user = userService.getUserByUsername(currentUser.getUsername());
        if (user == null) {
            logger.error("Utilisateur introuvable : {}", currentUser.getUsername());
            return "redirect:/login";
        }
        try {
            accountService.changeBalance(user, amount);
            logger.info("Versement réussi pour un montant de {}", amount);
            return "redirect:/transfer?addsuccess";
        } catch (Exception e) {
            logger.error("Erreur lors du versement pour un montant de {}: {}", amount, e.getMessage());
            return "redirect:/transfer?adderror";
        }
    }
}