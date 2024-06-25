package com.sajad.banking.BankingApp.model;

import com.sajad.banking.BankingApp.exception.account.BalanceNotEnoughException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table
@Getter
public class Account {

    /**
     * Account number!
     */
    @Id
    @GeneratedValue
    private Integer id;

    @Setter
    private BigDecimal balance;

    public void withdraw(BigDecimal amount) throws BalanceNotEnoughException {
        // Check over withdraw
        if (balance.compareTo(amount) < 0)
            throw new BalanceNotEnoughException();

        balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public Account() {
    }

    public Account(BigDecimal balance) {
        this.balance = balance;
    }
}
