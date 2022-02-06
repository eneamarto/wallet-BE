package com.example.walletbackend.services;

import com.example.walletbackend.DTO.BalanceResponse;
import com.example.walletbackend.DTO.TransactionRequest;
import com.example.walletbackend.entities.Transaction;
import com.example.walletbackend.entities.TransactionCategory;
import com.example.walletbackend.entities.User;
import com.example.walletbackend.exceptions.DateParsingException;
import com.example.walletbackend.exceptions.EmptyContainerException;
import com.example.walletbackend.repository.TransactionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger logger = LogManager.getLogger();
    private final TransactionRepository transactionRepository;
    private final TransactionCategoryService transactionCategoryService;
    private final UserService userService;
    private final String dateFormat = "yyyy-MM-dd";

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public TransactionService(TransactionRepository transactionRepository, TransactionCategoryService transactionCategoryService, UserService userService) {

        this.transactionRepository = transactionRepository;
        this.transactionCategoryService = transactionCategoryService;
        this.userService = userService;
    }

    private User getLoggedUser() {

        SecurityContext securityContext = SecurityContextHolder.getContext();

        UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();

        return userService.findByUsername(userDetails.getUsername());
    }

    /**
     * This method is used to convert a String object to an java.util.Date object.
     *
     * @param date The date given as a string in the default format yyyy-MM-dd.
     * @return It will return a java.util.Date object.
     */
    private Date parseDate(String date) throws DateParsingException {
        try {
            return new Date(simpleDateFormat.parse(date).getTime());
        } catch (ParseException e) {
            throw new DateParsingException("Parsing Date Failed. Date Entered: " + date + " possibly malformed. The default date format is " + dateFormat);
        }
    }

    /**
     * This method is used to get all the Transactions in a given time interval.
     *
     * @param start The starting date of the time interval given as a String.
     * @param end   The ending date of the time interval given as a String.
     * @return A list of Transaction type of all Transactions between the parameter range.
     */
    public List<Transaction> getTransactionInInterval(String start, String end) throws DateParsingException {

        Date startDate = parseDate(start);
        Date endDate = parseDate(end);

        logger.info("Filtering transactions on range " + start + " to " + end);

        return transactionRepository.findByTransactionDateIsBetweenAndTransactionUser(startDate, endDate, getLoggedUser());
    }

    public void addTransaction(TransactionRequest transactionRequest) throws DateParsingException {

        User user = getLoggedUser();

        if (transactionRequest.getTransactionAmount() == null) {
            logger.error("Transaction Amount value is NULL");
            throw new IllegalArgumentException("Transaction Amount cannot be NULL.");
        }

        Optional<TransactionCategory> transactionCategory = transactionCategoryService.findTransactionCategoryById(transactionRequest.getTransactionCategoryId());

        Transaction transaction = new Transaction(transactionRequest.getTransactionType(), transactionRequest.getTransactionAmount(),
                transactionRequest.getTransactionDescription(), parseDate(transactionRequest.getTransactionDate()));


        if (transactionCategory.isPresent() && user != null) {

            transaction.setTransactionCategory(transactionCategory.get());

            transaction.setTransactionUser(user);
        }

        transactionRepository.save(transaction);

        logger.info("Transaction with Amount: " + transaction.getTransactionAmount() + " of Type: " + transaction.getTransactionType() + " from User: " + user.getUserName() + " was successful.");
    }

    public List<Transaction> getTransactions() throws EmptyContainerException {

        List<Transaction> transactionList = getLoggedUser().getTransactionList();

        if (!transactionList.isEmpty()) {

            logger.info("All Transactions retrieved.");
            return transactionList;
        } else {

            logger.error("Transaction retrieval failed.");

            throw new EmptyContainerException("Transaction list is empty.");
        }

    }

    public boolean isInRange(Date testDate, Date startDate, Date endDate) {

        return testDate.getTime() >= startDate.getTime() &&
                testDate.getTime() <= endDate.getTime();
    }

    public Map<String, Double> transactionStatistics(String startDate, String endDate) throws EmptyContainerException, DateParsingException {

        Map<String, Double> transactionStatistics = new HashMap<>();

        List<Transaction> transactionList = getLoggedUser().getTransactionList();

        List<Transaction> filteredTransactionList = transactionList.stream()
                .filter(x -> {
                    try {
                        return isInRange(x.getTransactionDate(), parseDate(startDate), parseDate(endDate));
                    } catch (DateParsingException e) {
                        e.printStackTrace();
                    }
                    return false;
                }).collect(Collectors.toList());

        if (!filteredTransactionList.isEmpty()) {


            for (Transaction t : filteredTransactionList) {
                String transactionName = t.getTransactionCategory().getTransactionCategoryName();
                transactionStatistics.put(transactionName,
                        transactionStatistics.getOrDefault(transactionName, 0.0) + t.getTransactionAmount());
            }
        } else {

            logger.error("No Transactions between " + startDate + " and " + endDate + ".");
            throw new EmptyContainerException("No Transactions between " + startDate + " and " + endDate + ".");
        }

        if (!transactionStatistics.isEmpty()) {

            logger.info("Transaction Statistics retrieved.");
            return transactionStatistics;

        } else {

            logger.error("Transaction Statistics retrieval failed");
            throw new EmptyContainerException("Transaction Statistics HashMap is empty.");
        }
    }


    /**
     * This method converts a list of type Transaction into a CSV string which is in turn witten to a file with
     * a random filename in the resources folder.
     *
     * @param transactionList The list of Transactions to be written to a file.
     * @return It returns the filepath of the written file.
     * @throws IOException It throws an IOException in the case the file cannot be created.
     */
    public String transactionToCSV(List<Transaction> transactionList) throws IOException {

        BigInteger randomBigInteger = new BigInteger(128, new Random());

        Files.createDirectories(Paths.get("/remote/dir"));

        String filePath = "/remote/dir/transaction_history" + randomBigInteger + ".csv";

        List<List<String>> csvBody = transactionList.stream().
                map(x -> Arrays.asList(x.getTransactionType(), x.getTransactionAmount().toString(), x.getTransactionCategory().getTransactionCategoryName(), x.getTransactionDate().toString()))
                .collect(Collectors.toList());

        List<String> csvHeaders = Arrays.asList("Type", "Amount", "Category", "Date");

        String csvFile = String.join(" ,", csvHeaders) + "\n";

        csvFile = csvFile.concat(csvBody.stream().map(x -> String.join(" ,", x)).collect(Collectors.joining("\n")));

        fileWriter(csvFile, filePath);

        return filePath;
    }

    /**
     * This method writes a CSV String into a file.
     *
     * @param csvFile  The comma separated value string to be written to file.
     * @param filePath The file path of the file being written.
     * @throws IOException It throws an IOException in the case the file cannot be created.
     */
    private void fileWriter(String csvFile, String filePath) throws IOException {

        FileWriter writer = new FileWriter(filePath);

        writer.write(csvFile);
        writer.close();

    }

    public BalanceResponse calculateBalance(List<Transaction> transactionList) {
        double income = 0.0;
        double expense = 0.0;
        double balance = 0.0;

        for (Transaction transaction : transactionList) {

            if (transaction.getTransactionType().equals("income")) {

                income += transaction.getTransactionAmount();

            }

            if (transaction.getTransactionType().equals("expense")) {

                expense += transaction.getTransactionAmount();
            }
        }

        balance = income - expense;

        return new BalanceResponse(income, expense, balance);
    }

    public BalanceResponse getBalance() {

        User currentUser = getLoggedUser();

        List<Transaction> transactionList = currentUser.getTransactionList();

        return calculateBalance(transactionList);
    }

}
