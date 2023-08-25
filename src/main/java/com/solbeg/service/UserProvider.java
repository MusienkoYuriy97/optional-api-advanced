package com.solbeg.service;

import com.solbeg.model.User;

import java.util.Optional;

@FunctionalInterface
public interface UserProvider {
    Optional<User> getUser();
}
