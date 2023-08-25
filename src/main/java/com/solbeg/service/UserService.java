package com.solbeg.service;

import com.solbeg.model.User;

@FunctionalInterface
public interface UserService {
    void processUser(User user);

    default void processWithNoUser(){
        System.out.println("No user found");
    }
}
