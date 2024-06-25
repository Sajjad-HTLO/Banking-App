package com.sajad.banking.BankingApp.web.dto.account;

import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class NewAccountDto {

    @DecimalMin("1")
    private BigDecimal balance;

}
