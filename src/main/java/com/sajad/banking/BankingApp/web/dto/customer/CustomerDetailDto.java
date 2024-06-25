package com.sajad.banking.BankingApp.web.dto.customer;

import com.sajad.banking.BankingApp.model.Customer;
import com.sajad.banking.BankingApp.web.dto.account.AccountDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CustomerDetailDto {
    private Integer id;

    private Customer.CustomerType type;

    private String name;

    private String lastName;

    private String phoneNumber;

    private String faxNumber;

    private List<AccountDto> accountsDetail = new ArrayList<>();
}
