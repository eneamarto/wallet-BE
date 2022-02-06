package com.example.walletbackend.services;

import com.example.walletbackend.entities.User;
import com.example.walletbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {

        return userRepository.findByUserName(username);
    }

    public List<User> findAll() {

        return userRepository.findAll();
    }


}
