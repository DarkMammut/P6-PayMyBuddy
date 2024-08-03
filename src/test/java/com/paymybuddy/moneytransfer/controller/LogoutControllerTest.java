package com.paymybuddy.moneytransfer.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LogoutControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private LogoutController logoutController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogoutWithSession() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");

        String viewName = logoutController.logout(request);

        verify(session, times(1)).invalidate();
        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testLogoutWithoutSession() {
        when(request.getSession(false)).thenReturn(null);

        String viewName = logoutController.logout(request);

        verify(session, never()).invalidate();
        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testHandleGetLogoutWithSession() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");

        String viewName = logoutController.handleGetLogout(request);

        verify(session, times(1)).invalidate();
        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testHandleGetLogoutWithoutSession() {
        when(request.getSession(false)).thenReturn(null);

        String viewName = logoutController.handleGetLogout(request);

        verify(session, never()).invalidate();
        assertEquals("redirect:/login", viewName);
    }
}