package com.example.walletbackend.entities;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "transaction_category")
@Getter
@Setter
public class TransactionCategory {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tc_id", nullable = false)
    private Long id;

    @Column(name = "tc_type")
    private String transactionCategoryType;

    @Column(name = "tc_name")
    private String transactionCategoryName;

    @OneToMany(mappedBy = "transactionCategory")
    @Getter(AccessLevel.NONE)
    private List<Transaction> transactionList;

    public TransactionCategory(String transactionCategoryType, String transactionCategoryName) {
        this.transactionCategoryType = transactionCategoryType;
        this.transactionCategoryName = transactionCategoryName;
    }

    public TransactionCategory() {
    }
}
