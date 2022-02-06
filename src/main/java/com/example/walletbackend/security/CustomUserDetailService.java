package com.example.walletbackend.security;

import com.example.walletbackend.entities.User;
import com.example.walletbackend.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger();

    private final UserService userService;

    public CustomUserDetailService(UserService userService) {

        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        List<SimpleGrantedAuthority> roles = null;

        User user = userService.findByUsername(s);

        if (user != null) {

            logger.info("User " + user.getUserName() + " successfully loaded. With Role: " + user.getRole() + ".");

            roles = Arrays.asList(new SimpleGrantedAuthority(user.getRole()));

            return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), roles);
        }

        logger.error("User cannot be loaded");
        throw new UsernameNotFoundException("User Not Found");

    }

}