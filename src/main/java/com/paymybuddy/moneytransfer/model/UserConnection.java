package com.paymybuddy.moneytransfer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Entity
@Table(name = "UserConnection")
@Data
public class UserConnection {

    @Id
    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "connectedUserID")
    private User connectedUser;
}
