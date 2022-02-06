package com.example.walletbackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transaction")
@Getter
@Setter

public class Transaction {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tr_id", nullable = false)
    private Long id;

    @Column(name = "tr_type", nullable = false)
    private String transactionType;

    @Column(name = "tr_amount", nullable = false)
    private Double transactionAmount;

    @Column(name = "tr_description")
    private String transactionDescription;

    @Column(name = "tr_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    @ManyToOne
    @JoinColumn(name = "tr_category")
    private TransactionCategory transactionCategory;

    @ManyToOne
    @JoinColumn(name = "tr_user")
    @Getter(AccessLevel.NONE)
    private User transactionUser;

    public Transaction(String transactionType, Double transactionAmount, String transactionDescription, Date transactionDate) {
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.transactionDescription = transactionDescription;
        this.transactionDate = transactionDate;
    }

    public Transaction() {

    }
}
