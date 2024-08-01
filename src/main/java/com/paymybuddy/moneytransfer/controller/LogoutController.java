package com.paymybuddy.moneytransfer.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping
public class LogoutController {

    private static final Logger logger = LoggerFactory.getLogger(LogoutController.class);

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        logger.info("POST /logout called");
        HttpSession session = request.getSession(false);
        if (session != null) {
            logger.info("Invalidating session for user: {}", session.getAttribute("username"));
            session.invalidate();
        } else {
            logger.info("No session to invalidate");
        }
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String handleGetLogout(HttpServletRequest request) {
        logger.info("GET /logout called");
        HttpSession session = request.getSession(false);
        if (session != null) {
            logger.info("Invalidating session for user: {}", session.getAttribute("username"));
            session.invalidate();
        } else {
            logger.info("No session to invalidate");
        }
        return "redirect:/login";
    }
}
