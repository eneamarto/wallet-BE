package com.example.walletbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class BalanceHistoryResponse {

    private double value;

    private Date name;

}
