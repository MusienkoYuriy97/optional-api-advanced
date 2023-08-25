package com.solbeg.service;

import com.solbeg.model.UserBankAccount;

import java.util.Optional;

@FunctionalInterface
public interface UserBankAccountProvider {
    Optional<UserBankAccount> getUserBankAccount();
}
