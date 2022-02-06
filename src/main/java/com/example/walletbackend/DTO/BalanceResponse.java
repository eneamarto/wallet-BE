package com.example.walletbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class BalanceResponse {

    private double income;
    private double expenses;
    private double balance;

}
