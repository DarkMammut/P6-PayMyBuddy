package com.paymybuddy.moneytransfer.service;

import com.paymybuddy.moneytransfer.model.Account;
import com.paymybuddy.moneytransfer.model.Transaction;
import com.paymybuddy.moneytransfer.model.User;
import com.paymybuddy.moneytransfer.repository.AccountRepository;
import com.paymybuddy.moneytransfer.repository.TransactionRepository;
import com.paymybuddy.moneytransfer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    BigDecimal commissionRate = new BigDecimal("0.005");

    @Transactional
    public List<Transaction> getTransactionsByAccount(Account account) {
        logger.info("Fetching transactions for account: {}", account.getAccountID());
        List<Transaction> transactions = transactionRepository.findByAccount(account);
        logger.info("Fetched {} transactions for account: {}", transactions.size(), account.getAccountID());
        return transactions;
    }

    @Transactional
    public void processTransaction(int senderId, String receiverUsername, BigDecimal amount, String description) {
        logger.info("Processing transaction from user ID: {} to user: {} with amount: {}", senderId, receiverUsername, amount);

        User sender = userRepository.findByUserID(senderId);
        User receiver = userRepository.findByUsername(receiverUsername);
        BigDecimal commission = amount.multiply(commissionRate);
        BigDecimal totalAmount = amount.add(commission);

        if (sender != null && receiver != null) {
            logger.info("Found sender: {} and receiver: {}", sender.getUsername(), receiver.getUsername());

            Account senderAccount = accountRepository.findByUserID(sender);
            Account receiverAccount = accountRepository.findByUserID(receiver);

            if (senderAccount.getBalance().compareTo(totalAmount) >= 0) {
                logger.info("Sufficient balance: {}. Processing transaction...", senderAccount.getBalance());

                senderAccount.setBalance(senderAccount.getBalance().subtract(totalAmount));
                receiverAccount.setBalance(receiverAccount.getBalance().add(amount));

                accountRepository.save(senderAccount);
                accountRepository.save(receiverAccount);

                Transaction transaction = new Transaction();
                transaction.setSender(sender);
                transaction.setReceiver(receiverUsername);
                transaction.setAmount(amount);
                transaction.setDescription(description);
                transaction.setAccount(senderAccount);

                transactionRepository.save(transaction);

                logger.info("Transaction processed successfully: {} -> {} ({} {})", sender.getUsername(), receiver.getUsername(), amount, description);
            } else {
                logger.error("Insufficient balance: {}. Transaction aborted.", senderAccount.getBalance());
                throw new RuntimeException("Balance insuffisante. Transaction annulée.");
            }
        } else {
            if (sender == null) {
                logger.error("Sender with ID: {} not found", senderId);
                throw new RuntimeException("Utilisateur inconnu. Transaction annulée.");
            } else {
                logger.error("Receiver with username: {} not found", receiverUsername);
                throw new RuntimeException("Destinataire inconnu. Transaction annulée.");
            }
        }
    }
}