package com.example.walletbackend.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "balance_history")
@Getter
@Setter
@NoArgsConstructor
public class BalanceHistory {

    @Getter(AccessLevel.NONE)
    @ManyToOne
    @JoinColumn(name = "user_id")
    User balanceHistoryUser;

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "time_stamp")

    private Date timeStamp;
    @Column(name = "amount")
    private double amount;

    public BalanceHistory(Date timeStamp, double amount, User balanceHistoryUser) {
        this.timeStamp = timeStamp;
        this.amount = amount;
        this.balanceHistoryUser = balanceHistoryUser;
    }
}
