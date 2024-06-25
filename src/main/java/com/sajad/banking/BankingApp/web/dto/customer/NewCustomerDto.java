package com.sajad.banking.BankingApp.web.dto.customer;

import com.sajad.banking.BankingApp.model.Customer.CustomerType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewCustomerDto {

    @NotNull
    private String name;

    @NotNull
    private CustomerType type;

    private String lastName;

    @NotNull
    private String phoneNumber;

    private String faxNumber;

    @DecimalMin("0")
    private BigDecimal initialBalance;
}
