package com.paymybuddy.moneytransfer.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "UserConnection")
@Data
@IdClass(UserConnectionId.class)
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
