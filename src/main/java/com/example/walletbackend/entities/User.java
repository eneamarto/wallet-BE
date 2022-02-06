package com.example.walletbackend.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name="user_name",nullable = false)
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_balance")
    private Double userBalance;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "transactionUser")
    private List<Transaction> transactionList;

    @OneToMany(mappedBy = "balanceHistoryUser")
    private List<BalanceHistory> balanceHistoryList;

}
