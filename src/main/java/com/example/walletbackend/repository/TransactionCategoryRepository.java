package com.example.walletbackend.repository;

import com.example.walletbackend.entities.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory,Long> {

    List<TransactionCategory> findByTransactionCategoryType(String type);

}
