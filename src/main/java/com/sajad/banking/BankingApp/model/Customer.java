package com.sajad.banking.BankingApp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.sajad.banking.BankingApp.model.Customer.CustomerType.*;

@Entity
@Table
@Getter
public class Customer {

    public Customer() {
    }

    public Customer(String name, String phoneNumber, CustomerType type, String lastNameOrFaxNumber, BigDecimal balance) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;

        if (this.type.equals(REAL)) {
            this.lastName = lastNameOrFaxNumber;

        } else if (this.type.equals(LEGAL)) {
            this.faxNumber = lastNameOrFaxNumber;

        } else throw new IllegalArgumentException();

        this.getAccounts().add(new Account(balance));
    }

    @Id
    @GeneratedValue
    private Integer id;

    @Setter
    private CustomerType type;

    @Setter
    private String name;

    @Setter
    private String lastName;

    @Setter
    private String phoneNumber;

    @Setter
    private String faxNumber;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    public enum CustomerType {
        REAL, LEGAL
    }
}


