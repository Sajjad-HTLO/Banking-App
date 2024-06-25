package com.sajad.banking.BankingApp.web.utility;

import com.sajad.banking.BankingApp.model.Account;
import com.sajad.banking.BankingApp.model.Customer;
import com.sajad.banking.BankingApp.model.Customer.CustomerType;
import com.sajad.banking.BankingApp.web.dto.account.AccountDto;
import com.sajad.banking.BankingApp.web.dto.customer.CustomerDetailDto;
import com.sajad.banking.BankingApp.web.dto.account.NewAccountDto;
import com.sajad.banking.BankingApp.web.dto.customer.CustomerListDto;
import com.sajad.banking.BankingApp.web.dto.customer.NewCustomerDto;

public class Utility {

    public static Customer toCustomer(NewCustomerDto newDto) {
        Customer customer;
        if (newDto.getType().equals(CustomerType.REAL)) {
            customer = new Customer(newDto.getName(), newDto.getPhoneNumber(), newDto.getType(), newDto.getLastName(), newDto.getInitialBalance());
        } else if (newDto.getType().equals(CustomerType.LEGAL)) {
            customer = new Customer(newDto.getName(), newDto.getPhoneNumber(), newDto.getType(), newDto.getFaxNumber(), newDto.getInitialBalance());
        } else throw new IllegalArgumentException();

        return customer;
    }

    public static Account toAccount(NewAccountDto newAccountDto) {
        Account account = new Account();
        account.setBalance(newAccountDto.getBalance());

        return account;
    }

    public static AccountDto toAccountDto(Account account) {
        return new AccountDto(account.getId(), account.getBalance());
    }

    public static CustomerDetailDto toCustomerDetailDto(Customer customer) {
        CustomerDetailDto detailDto = new CustomerDetailDto();
        detailDto.setId(customer.getId());
        detailDto.setName(customer.getName());
        detailDto.setPhoneNumber(customer.getPhoneNumber());
        detailDto.setType(customer.getType());

        if (customer.getType().equals(CustomerType.REAL))
            detailDto.setLastName(customer.getLastName());
        else
            detailDto.setFaxNumber(customer.getFaxNumber());

        // Set accounts
        customer.getAccounts().forEach(account ->
                detailDto.getAccountsDetail().add(new AccountDto(account.getId(), account.getBalance())));

        return detailDto;
    }

    public static CustomerListDto toCustomerListDto(Customer customer) {
        CustomerListDto listDto = new CustomerListDto();
        listDto.setId(customer.getId());
        listDto.setName(customer.getName());
        listDto.setType(customer.getType());

        return listDto;
    }
}
