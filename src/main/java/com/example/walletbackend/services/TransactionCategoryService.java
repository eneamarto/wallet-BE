package com.example.walletbackend.services;

import com.example.walletbackend.entities.TransactionCategory;
import com.example.walletbackend.repository.TransactionCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionCategoryService {

    private final TransactionCategoryRepository transactionCategoryRepository;


    public TransactionCategoryService(TransactionCategoryRepository transactionCategoryRepository) {
        this.transactionCategoryRepository = transactionCategoryRepository;
    }

    public Optional<TransactionCategory> findTransactionCategoryById(Long id){

        return transactionCategoryRepository.findById(id);
    }

    public List<TransactionCategory> findTransactionCategoryByType(String type){

        return transactionCategoryRepository.findByTransactionCategoryType(type);
    }

    public List<TransactionCategory> findAll(){

        return transactionCategoryRepository.findAll();
    }

    public TransactionCategory createCategory(TransactionCategory transactionCategory){

        transactionCategoryRepository.save(transactionCategory);

        return transactionCategory;
    }

}
