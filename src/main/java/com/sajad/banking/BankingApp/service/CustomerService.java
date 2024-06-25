package com.sajad.banking.BankingApp.service;

import com.sajad.banking.BankingApp.exception.account.*;
import com.sajad.banking.BankingApp.exception.customer.CustomerDeleteException;
import com.sajad.banking.BankingApp.exception.customer.CustomerExistsException;
import com.sajad.banking.BankingApp.exception.customer.CustomerNotFoundException;
import com.sajad.banking.BankingApp.model.Account;
import com.sajad.banking.BankingApp.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Optional<Customer> findCustomer(Integer customerId);

    Page<Customer> listCustomer(Pageable pageable);

    void createNewCustomer(Customer customer) throws CustomerExistsException;

    List<Account> listCustomerAccounts(Integer customerId) throws CustomerNotFoundException;

    void deleteCustomer(Integer customerId) throws CustomerNotFoundException, CustomerDeleteException;

    void addCustomerAccount(Integer customerId, Account account) throws CustomerNotFoundException;

    void deleteCustomerAccount(Integer customerId, Integer accountId) throws CustomerNotFoundException, AccountNotFoundException,
            BalanceAccountDeleteException, OnlyAccountDeleteException;

    void transfer(Integer customerId, Integer fromAccountId, Integer toAccountId, BigDecimal amount)
            throws CustomerNotFoundException, AccountNotFoundException, BalanceNotEnoughException, NegativeAmountException, InvalidTransferDetails;

    void deposit(Integer customerId, Integer accountId, BigDecimal amount) throws CustomerNotFoundException, AccountNotFoundException, NegativeAmountException;

    void withdraw(Integer customerId, Integer accountId, BigDecimal amount) throws CustomerNotFoundException,
            AccountNotFoundException, NegativeAmountException, BalanceNotEnoughException;
}
