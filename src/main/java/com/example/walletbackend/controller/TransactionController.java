package com.example.walletbackend.controller;

import com.example.walletbackend.DTO.BalanceHistoryResponse;
import com.example.walletbackend.DTO.BalanceResponse;
import com.example.walletbackend.DTO.DateIntervalRequest;
import com.example.walletbackend.DTO.TransactionRequest;
import com.example.walletbackend.entities.Transaction;
import com.example.walletbackend.exceptions.DateParsingException;
import com.example.walletbackend.exceptions.EmptyContainerException;
import com.example.walletbackend.services.BalanceHistoryService;
import com.example.walletbackend.services.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TransactionController {

    private static final Logger logger = LogManager.getLogger();
    private final TransactionService transactionService;
    private final BalanceHistoryService balanceHistoryService;


    public TransactionController(TransactionService transactionService, BalanceHistoryService balanceHistoryService) {

        this.transactionService = transactionService;
        this.balanceHistoryService = balanceHistoryService;
    }

    @GetMapping("/transactions/{start}_{end}")
    public List<Transaction> getTransactions(@PathVariable("start") @DateTimeFormat(pattern = "yyyy-MM-dd") String start, @PathVariable("end") @DateTimeFormat(pattern = "yyyy-MM-dd") String end) throws DateParsingException {

        return transactionService.getTransactionInInterval(start, end);
    }


    @PostMapping("/transactions")
    public void addTransaction(@RequestBody TransactionRequest transactionRequest) throws DateParsingException {

        transactionService.addTransaction(transactionRequest);

    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions() throws EmptyContainerException {

        return transactionService.getTransactions();
    }

    @PostMapping("/transaction_stats/")
    public Map<String, Double> getTransactionStatistics(@RequestBody DateIntervalRequest dateIntervalRequest) throws EmptyContainerException, DateParsingException {

        String startDate = dateIntervalRequest.getStartDate();

        String endDate = dateIntervalRequest.getEndDate();

        logger.info("Showing Transaction Statistics between dates: " + startDate + " and " + endDate + ".");

        return transactionService.transactionStatistics(startDate, endDate);

    }

    @GetMapping("/transaction_export")
    public ResponseEntity test() throws IOException, EmptyContainerException {

        List<Transaction> transactionList = transactionService.getTransactions();

        File file = new File(transactionService.transactionToCSV(transactionList));

        logger.info("Temporary CSV file created");

        Path path = Paths.get(file.getAbsolutePath());

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transaction_history.csv");

        long fileLength = file.length();

        boolean isDeleted = file.delete();

        if (isDeleted) {

            logger.info("Temporary file successfully deleted.");

        } else {

            logger.error("Temporary file deletion failed.");
        }

        if (resource.contentLength() != 0) {

            logger.info("Transaction History Response successful.");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fileLength)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else {
            logger.error("Transaction History Response failed.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Resource empty");
        }
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance() {

        logger.info("Showing Balance Information.");

        return transactionService.getBalance();
    }

    @GetMapping("/balance_history")
    public List<BalanceHistoryResponse> getBalanceHistory() {

        logger.info("Showing Balance History");

        return balanceHistoryService.getBalanceHistory();

    }
}
