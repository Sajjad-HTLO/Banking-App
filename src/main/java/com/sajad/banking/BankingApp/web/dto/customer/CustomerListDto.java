package com.sajad.banking.BankingApp.web.dto.customer;

import com.sajad.banking.BankingApp.model.Customer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerListDto {

    private Integer id;

    private Customer.CustomerType type;

    private String name;
}
