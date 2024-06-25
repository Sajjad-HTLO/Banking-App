package com.sajad.banking.BankingApp.service;

import com.sajad.banking.BankingApp.exception.account.*;
import com.sajad.banking.BankingApp.exception.customer.CustomerDeleteException;
import com.sajad.banking.BankingApp.exception.customer.CustomerExistsException;
import com.sajad.banking.BankingApp.exception.customer.CustomerNotFoundException;
import com.sajad.banking.BankingApp.model.Account;
import com.sajad.banking.BankingApp.model.Customer;
import com.sajad.banking.BankingApp.repository.account.AccountRepository;
import com.sajad.banking.BankingApp.repository.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SimpleCustomerService implements CustomerService {

    private final CustomerRepository customerRepository;

    private final AccountRepository accountRepository;

    @Autowired
    public SimpleCustomerService(CustomerRepository customerRepository, AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Customer> findCustomer(Integer customerId) {
        return this.customerRepository.findById(customerId);
    }

    @Override
    public Page<Customer> listCustomer(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    /**
     * Creates a new customer.
     *
     * @param newCustomer New customer object to persist.
     * @throws CustomerExistsException If there is a customer with the same phone number.
     */
    @Override
    @Transactional
    public void createNewCustomer(Customer newCustomer) throws CustomerExistsException {
        // Check if duplicate customer via phone number
        if (customerRepository.findByPhoneNumber(newCustomer.getPhoneNumber()).stream().findFirst().isPresent())
            throw new CustomerExistsException();

        customerRepository.save(newCustomer);
    }

    @Override
    public List<Account> listCustomerAccounts(Integer customerId) throws CustomerNotFoundException {
        return customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new).getAccounts();
    }

    /**
     * Delete a customer.
     * The customer's accounts should have zero balance.
     *
     * @param customerId Customer identifier which we want to remove it.
     * @throws CustomerNotFoundException There is no customer with the provided id.
     * @throws CustomerDeleteException   Customer needs to empty all of his/her accounts.
     */
    @Override
    public void deleteCustomer(Integer customerId) throws CustomerNotFoundException, CustomerDeleteException {
        Customer customer = this.customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);

        // Customer can be deleted if exists and all of his/her accounts' balances be zero
        if (customer.getAccounts().stream().anyMatch(account -> account.getBalance().compareTo(BigDecimal.ZERO) > 0))
            throw new CustomerDeleteException();

        // Safe to delete customer
        customerRepository.delete(customer);
    }

    @Override
    @Transactional
    public void addCustomerAccount(Integer customerId, Account account) throws CustomerNotFoundException {
        Customer customer = this.customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);

        // Add new account for this customer
        customer.getAccounts().add(account);
    }

    @Override
    @Transactional
    public void deleteCustomerAccount(Integer customerId, Integer accountId) throws CustomerNotFoundException,
            AccountNotFoundException, BalanceAccountDeleteException, OnlyAccountDeleteException {
        Customer customer = this.customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);

        // Every customer should have at least one account
        if (customer.getAccounts().size() == 1)
            throw new OnlyAccountDeleteException();

        Account accountToDelete = customer.getAccounts().stream().filter(account -> account.getId().equals(accountId))
                .findAny().orElseThrow(AccountNotFoundException::new);

        // Complain if the account have balance
        if (accountToDelete.getBalance().compareTo(BigDecimal.ZERO) > 0)
            throw new BalanceAccountDeleteException();

        // Safe to delete account
        customer.getAccounts().removeIf(account -> account.getId().equals(accountId));
    }
    @Override
    @Transactional
    public void transfer(Integer customerId, Integer fromAccountId, Integer toAccountId, BigDecimal amount)
            throws CustomerNotFoundException, AccountNotFoundException, BalanceNotEnoughException, NegativeAmountException, InvalidTransferDetails {

        // Check accounts are different
        if (Objects.equals(fromAccountId, toAccountId))
            throw new InvalidTransferDetails();

        // Check negative amount
        checkAmount(amount);

        // Validate customer
        Customer customer = customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);

        // Validate original account
        customer.getAccounts().stream().filter(account ->
                Objects.equals(account.getId(), fromAccountId)).findFirst().orElseThrow(AccountNotFoundException::new);

        // We need to fetch originalAccount from repository to be able to exclusively lock on it as well
        Account originalAccount = accountRepository.findById(fromAccountId).orElseThrow(AccountNotFoundException::new);

        // Find destination account, while locked for update
        Account destinationAccount = accountRepository.findById(toAccountId).orElseThrow(AccountNotFoundException::new);

        // Now, we have both accounts ready and locked, safe to perform the transfer
        originalAccount.withdraw(amount);
        destinationAccount.deposit(amount);
    }

    @Override
    @Transactional
    public void deposit(Integer customerId, Integer accountId, BigDecimal amount) throws CustomerNotFoundException,
            AccountNotFoundException, NegativeAmountException {
        checkAmount(amount);

        Customer customer = customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);

        // Validate account owner
        customer.getAccounts().stream().filter(account ->
                Objects.equals(account.getId(), accountId)).findFirst().orElseThrow(AccountNotFoundException::new);

        Account account = accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);

        account.deposit(amount);
    }

    @Override
    @Transactional
    public void withdraw(Integer customerId, Integer accountId, BigDecimal amount) throws CustomerNotFoundException,
            AccountNotFoundException, NegativeAmountException, BalanceNotEnoughException {
        // Check negative amount
        checkAmount(amount);

        Customer customer = customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);

        // Validate account owner
        customer.getAccounts().stream().filter(account ->
                Objects.equals(account.getId(), accountId)).findFirst().orElseThrow(AccountNotFoundException::new);

        Account account = accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);

        account.withdraw(amount);
    }

    private static void checkAmount(BigDecimal amount) throws NegativeAmountException {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException();
        }
    }
}
