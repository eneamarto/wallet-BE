package com.example.walletbackend.services;


import com.example.walletbackend.entities.BalanceHistory;
import com.example.walletbackend.entities.Transaction;
import com.example.walletbackend.entities.User;
import com.example.walletbackend.repository.BalanceHistoryRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class BalanceHistoryTask {

    private static final Logger logger = LogManager.getLogger();
    private final UserService userService;
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final String dateFormat = "yyyy-MM-dd";

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public BalanceHistoryTask(UserService userService, BalanceHistoryRepository balanceHistoryRepository) {
        this.userService = userService;
        this.balanceHistoryRepository = balanceHistoryRepository;
    }

    public double calculateBalance(List<Transaction> transactionList) {
        double balance = 0.0;

        for (Transaction transaction : transactionList) {

            if (transaction.getTransactionType().equals("income")) {

                balance += transaction.getTransactionAmount();

            } else if (transaction.getTransactionType().equals("expense")) {

                balance -= transaction.getTransactionAmount();
            }
        }
        return balance;
    }

    @Scheduled(cron = "@daily")
    public void updateBalance() {

        List<User> userList = userService.findAll();

        for (User currentUser : userList) {

            double amount = calculateBalance(currentUser.getTransactionList());

            BalanceHistory historyEntry = new BalanceHistory(new Date(), amount, currentUser);

            balanceHistoryRepository.save(historyEntry);

            logger.info("Balance info of User: " + currentUser.getUserName() + " last updated at: " + simpleDateFormat.format(new Date()));

        }
    }

}
