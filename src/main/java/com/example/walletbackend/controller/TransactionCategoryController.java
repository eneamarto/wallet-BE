package com.example.walletbackend.controller;

import com.example.walletbackend.DTO.TransactionCategoryRequest;
import com.example.walletbackend.entities.TransactionCategory;
import com.example.walletbackend.exceptions.CategoryDetailsException;
import com.example.walletbackend.services.TransactionCategoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TransactionCategoryController {

    private final TransactionCategoryService transactionCategoryService;
    private static final Logger logger = LogManager.getLogger();


    public TransactionCategoryController(TransactionCategoryService transactionCategoryService) {
        this.transactionCategoryService = transactionCategoryService;
    }


    @GetMapping("/categories")
    public List<TransactionCategory> getCategories() {

        logger.info("Showing all Categories.");
        return transactionCategoryService.findAll();

    }

    @GetMapping("/categories/{type}")
    public List<TransactionCategory> getCategoriesByType(@PathVariable("type") String type) {


        logger.info("Filter Categories of Type: " + type + ".");

        return transactionCategoryService.findTransactionCategoryByType(type);

    }

    @RequestMapping(value = "/categories/", method = RequestMethod.POST)
    public void addCategory(@RequestBody TransactionCategoryRequest categoryRequest) throws CategoryDetailsException {

        String type = categoryRequest.getType();
        String name = categoryRequest.getName();

        if (type.isBlank() || name.isBlank()) {

            logger.error("Type or Name values for new Category are empty.");

            throw new CategoryDetailsException("No values were provided for Category Type or Name.");
        } else {

            TransactionCategory newCategory = new TransactionCategory(type, name);

            TransactionCategory addedCategory = transactionCategoryService.createCategory(newCategory);

            if (addedCategory != null) {

                logger.info("New Transaction Category created.");

            } else {

                logger.error("Transaction Category creation failed");
            }
        }
    }
}
