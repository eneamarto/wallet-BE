package com.example.walletbackend.DTO;

import lombok.Getter;

@Getter
public class TransactionRequest {

    private String transactionType;

    private Double transactionAmount;

    private String transactionDescription;

    private String transactionDate;

    private Long transactionCategoryId;

    private String transactionUsername;

}
