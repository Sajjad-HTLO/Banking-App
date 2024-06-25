package com.sajad.banking.BankingApp.web.dto.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferDto {

    @NotNull
    private Integer fromAccountId;

    @NotNull
    private Integer toAccountId;

    @DecimalMin("1")
    private BigDecimal amount;


}
