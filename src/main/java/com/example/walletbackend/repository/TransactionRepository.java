package com.example.walletbackend.repository;

import com.example.walletbackend.entities.Transaction;
import com.example.walletbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {


    List<Transaction> findByTransactionDateIsBetweenAndTransactionUser(Date transactionDate, Date transactionDate2, User user);


}
