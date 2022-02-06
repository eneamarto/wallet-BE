package com.example.walletbackend.controller;

import com.example.walletbackend.DTO.AuthenticationResponse;
import com.example.walletbackend.DTO.UserRequest;
import com.example.walletbackend.security.CustomUserDetailService;
import com.example.walletbackend.security.JwtUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/auth/signIn")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody UserRequest userRequest)
            throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userRequest.getUsername(), userRequest.getPassword()));

            logger.info("User: "+ userRequest.getUsername()+" Successfully Signed In.");

        } catch (BadCredentialsException e) {
            logger.error("Invalid Credentials. Sign In Failed.");
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        UserDetails userdetails = userDetailsService.loadUserByUsername(userRequest.getUsername());

        String token = jwtUtil.generateToken(userdetails);

        logger.info("Access token Generated.");

        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

}
