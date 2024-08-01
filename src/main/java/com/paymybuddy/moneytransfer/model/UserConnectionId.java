package com.paymybuddy.moneytransfer.model;

import java.io.Serializable;
import java.util.Objects;

public class UserConnectionId implements Serializable {
    private User user;
    private User connectedUser;

    // Default constructor
    public UserConnectionId() {}

    // Constructor
    public UserConnectionId(User user, User connectedUser) {
        this.user = user;
        this.connectedUser = connectedUser;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getConnectedUser() {
        return connectedUser;
    }

    public void setConnectedUser(User connectedUser) {
        this.connectedUser = connectedUser;
    }

    // hashCode and equals methods
    @Override
    public int hashCode() {
        return Objects.hash(user, connectedUser);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserConnectionId that = (UserConnectionId) o;
        return Objects.equals(user, that.user) && Objects.equals(connectedUser, that.connectedUser);
    }
}