package com.solbeg.service;

import com.solbeg.model.User;

import java.math.BigDecimal;

public class Users {
    public static User generateUser() {
        return User.builder()
                .id(1L)
                .name("John")
                .email("m@gmail.com")
                .balance(BigDecimal.TEN)
                .build();
    }
}
