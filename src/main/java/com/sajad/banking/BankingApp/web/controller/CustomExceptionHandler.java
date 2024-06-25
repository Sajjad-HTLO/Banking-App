package com.sajad.banking.BankingApp.web.controller;

import com.sajad.banking.BankingApp.exception.account.*;
import com.sajad.banking.BankingApp.exception.customer.CustomerDeleteException;
import com.sajad.banking.BankingApp.exception.customer.CustomerExistsException;
import com.sajad.banking.BankingApp.exception.customer.CustomerNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Application-wide exception handler advice to provide customized error messages.
 *
 * @author Sajad
 */
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    protected ResponseEntity<String> handleAccountNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(BalanceNotEnoughException.class)
    protected ResponseEntity<String> handleBalanceNotEnoughException() {
        String body = "Balance is not enough for this transaction!";

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(CustomerExistsException.class)
    protected ResponseEntity<String> handleCustomerExistsException() {
        String body = "Customer already registered with this phone number!";

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    protected ResponseEntity<String> handleCustomerNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(NegativeAmountException.class)
    protected ResponseEntity<String> handleNegativeAmountException() {
        String body = "Invalid amount!";
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(BalanceAccountDeleteException.class)
    protected ResponseEntity<String> handleBalanceAccountDeleteException() {
        String body = "Cannot delete an this account due to its balance!";

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(OnlyAccountDeleteException.class)
    protected ResponseEntity<String> handleOnlyAccountDeleteException() {
        String body = "You should have at least one account!";

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(CustomerDeleteException.class)
    protected ResponseEntity<String> handleCustomerDeleteException() {
        String body = "Customer should not have any positive balance account!";

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InvalidTransferDetails.class)
    protected ResponseEntity<String> handleInvalidTransferDetails() {
        String body = "Account details are wrong!";

        return ResponseEntity.badRequest().body(body);
    }
}
