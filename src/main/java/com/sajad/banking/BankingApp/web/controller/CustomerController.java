package com.sajad.banking.BankingApp.web.controller;

import com.sajad.banking.BankingApp.exception.account.*;
import com.sajad.banking.BankingApp.exception.customer.CustomerDeleteException;
import com.sajad.banking.BankingApp.exception.customer.CustomerExistsException;
import com.sajad.banking.BankingApp.exception.customer.CustomerNotFoundException;
import com.sajad.banking.BankingApp.model.Customer;
import com.sajad.banking.BankingApp.service.CustomerService;
import com.sajad.banking.BankingApp.web.dto.account.AccountDto;
import com.sajad.banking.BankingApp.web.dto.account.DepositDto;
import com.sajad.banking.BankingApp.web.dto.account.TransferDto;
import com.sajad.banking.BankingApp.web.dto.account.NewAccountDto;
import com.sajad.banking.BankingApp.web.dto.customer.CustomerListDto;
import com.sajad.banking.BankingApp.web.dto.customer.NewCustomerDto;
import com.sajad.banking.BankingApp.web.utility.Utility;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Provides a REST-API for customer and account management.
 *
 * @author Sajad
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    /**
     * Appointment service instance
     */
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<?> listCustomers(@PageableDefault(sort = "id") Pageable pageable) {
        List<CustomerListDto> customers = customerService.listCustomer(pageable)
                .stream().map(Utility::toCustomerListDto).collect(Collectors.toList());

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findCustomer(@PathVariable("id") Integer customerId) {
        Optional<Customer> customerOptional = customerService.findCustomer(customerId);

        return ResponseEntity.ok(customerOptional.stream().map(Utility::toCustomerDetailDto));
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody @Valid NewCustomerDto customerDto) throws CustomerExistsException {
        customerService.createNewCustomer(Utility.toCustomer(customerDto));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("id") Integer customerId) throws CustomerNotFoundException,
            CustomerDeleteException {
        customerService.deleteCustomer(customerId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/accounts")
    public ResponseEntity<?> addNewCustomerAccount(@PathVariable("id") Integer customerId, @RequestBody @Valid NewAccountDto newAccountDto)
            throws CustomerNotFoundException {
        customerService.addCustomerAccount(customerId, Utility.toAccount(newAccountDto));

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/accounts")
    public ResponseEntity<?> listCustomerAccounts(@PathVariable("id") Integer customerId) throws CustomerNotFoundException {
        List<AccountDto> customerAccounts = customerService.listCustomerAccounts(customerId)
                .stream()
                .map(Utility::toAccountDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(customerAccounts);
    }

    @DeleteMapping("/{id}/accounts/{accountId}")
    public ResponseEntity<?> deleteCustomerAccount(@PathVariable("id") Integer customerId, @PathVariable("accountId") Integer accountId)
            throws CustomerNotFoundException, BalanceAccountDeleteException, AccountNotFoundException, OnlyAccountDeleteException {
        customerService.deleteCustomerAccount(customerId, accountId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/transfer")
    public ResponseEntity<?> transfer(@PathVariable("id") Integer customerId, @RequestBody @Valid TransferDto transferDto)
            throws CustomerNotFoundException, AccountNotFoundException, BalanceNotEnoughException, NegativeAmountException, InvalidTransferDetails {
        customerService.transfer(customerId, transferDto.getFromAccountId(), transferDto.getToAccountId(), transferDto.getAmount());

        return ResponseEntity.ok("Transaction was successful");
    }

    @PutMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable("id") Integer customerId, @RequestBody @Valid DepositDto depositDto)
            throws CustomerNotFoundException, AccountNotFoundException, NegativeAmountException {
        customerService.deposit(customerId, depositDto.getAccountId(), depositDto.getAmount());

        return ResponseEntity.ok("Transaction was successful");
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable("id") Integer customerId, @RequestBody @Valid DepositDto depositDto)
            throws CustomerNotFoundException, AccountNotFoundException, NegativeAmountException, BalanceNotEnoughException {
        customerService.withdraw(customerId, depositDto.getAccountId(), depositDto.getAmount());

        return ResponseEntity.ok("Transaction was successful");
    }
}