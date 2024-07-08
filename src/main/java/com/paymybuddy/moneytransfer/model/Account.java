package com.paymybuddy.moneytransfer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accountID")
    private Integer accountID;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name= "userID")
    private User userID;

    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Transaction transaction;
}
