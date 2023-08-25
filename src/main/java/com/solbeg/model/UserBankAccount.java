package com.solbeg.model;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Optional;

@NoArgsConstructor
@Setter
public class UserBankAccount extends User {
    private BigDecimal creditBalance;

    public UserBankAccount(User user, BigDecimal creditBalance) {
        super(user.getId(), user.getName(), user.getEmail(), user.getBalance());
        this.creditBalance = creditBalance;
    }

    public Optional<BigDecimal> getCreditBalance() {
        return Optional.ofNullable(creditBalance);
    }
}
