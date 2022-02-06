package com.example.walletbackend.services;

import com.example.walletbackend.DTO.BalanceHistoryResponse;
import com.example.walletbackend.entities.BalanceHistory;
import com.example.walletbackend.entities.User;
import com.example.walletbackend.repository.BalanceHistoryRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BalanceHistoryService {

    private final BalanceHistoryRepository balanceHistoryRepository;
    private final UserService userService;

    public BalanceHistoryService(BalanceHistoryRepository balanceHistoryRepository, UserService userService) {
        this.balanceHistoryRepository = balanceHistoryRepository;
        this.userService = userService;
    }

    private User getLoggedUser() {

        SecurityContext securityContext = SecurityContextHolder.getContext();

        UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();

        return userService.findByUsername(userDetails.getUsername());
    }


    public List<BalanceHistoryResponse> getBalanceHistory() {

        User currentUser = getLoggedUser();

        List<BalanceHistory> balanceHistoryList = currentUser.getBalanceHistoryList();

        List<BalanceHistoryResponse> balanceHistoryResponses = new ArrayList<>();

        for (BalanceHistory balanceEntry : balanceHistoryList) {

            balanceHistoryResponses.add(new BalanceHistoryResponse(balanceEntry.getAmount(), balanceEntry.getTimeStamp()));
        }
        return balanceHistoryResponses;
    }

}
