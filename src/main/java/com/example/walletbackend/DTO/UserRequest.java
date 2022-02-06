package com.example.walletbackend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    private String username;

    private String password;

    private String role;

}
