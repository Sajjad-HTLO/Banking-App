package com.sajad.banking.BankingApp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sajad.banking.BankingApp.web.dto.account.AccountDto;
import com.sajad.banking.BankingApp.web.dto.account.NewAccountDto;
import com.sajad.banking.BankingApp.web.dto.account.TransferDto;
import com.sajad.banking.BankingApp.web.dto.customer.NewCustomerDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerAccountIntegrationTests extends TestBase {

    @Test
    public void createNewAccount_CustomerExists_ShouldBeSuccessful() throws JsonProcessingException {
        // First, create a new customer
        HttpEntity<NewCustomerDto> request = getDummyNewCustomerRequest(100);

        ResponseEntity<String> postResult = this.restTemplate.postForEntity(getBaseUrl() + "/api/customers", request, String.class);
        assertThat(postResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        HttpEntity<NewAccountDto> accountCreationRequest = getDummyNewAccountRequest(1000);

        // second, create an account for him/her
        ResponseEntity<String> accountCreationResult = this.restTemplate.postForEntity(getBaseUrl() + "/api/customers/1/accounts", accountCreationRequest, String.class);
        assertThat(accountCreationResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Verify account creation
        ResponseEntity<String> getResult = this.restTemplate.getForEntity(getBaseUrl() + "/api/customers/1/accounts", String.class);
        assertThat(getResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        AccountDto[] accounts = objectMapper.readValue(getResult.getBody(), AccountDto[].class);
        assertThat(accounts.length).isEqualTo(2);
    }

    @Test
    public void listCustomerAccount_CustomerNotExist_ShouldReturnError() {
        HttpClientErrorException.NotFound response = assertThrows(HttpClientErrorException.NotFound.class, () ->
                this.restTemplate.getForEntity(getBaseUrl() + "/api/customers/1/accounts", String.class));
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteNewAccount_CustomerHasOneAccount_ShouldReturnError() {
        // First, create a new customer
        HttpEntity<NewCustomerDto> request = getDummyNewCustomerRequest(100);

        ResponseEntity<String> postResult = this.restTemplate.postForEntity(getBaseUrl() + "/api/customers", request, String.class);
        assertThat(postResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Try to delete the only account
        HttpClientErrorException.BadRequest errorResponse = assertThrows(HttpClientErrorException.BadRequest.class, () ->
                this.restTemplate.delete(getBaseUrl() + "/api/customers/1/accounts/1", String.class));
        assertEquals(errorResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNotNull(errorResponse.getMessage());
        assertEquals(errorResponse.getMessage(), "400 : \"You should have at least one account!\"");
    }

    @Test
    public void deleteCustomerAccount_CustomerNotExist_ShouldReturnError() {
        HttpClientErrorException.NotFound response = assertThrows(HttpClientErrorException.NotFound.class, () ->
                this.restTemplate.delete(getBaseUrl() + "/api/customers/1/accounts/1", String.class));
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void transfer_CustomerHasTwoAccounts_ShouldBeSuccessful() throws JsonProcessingException {
        // First, create a new customer
        HttpEntity<NewCustomerDto> request = getDummyNewCustomerRequest(2000);

        ResponseEntity<String> postResult = this.restTemplate.postForEntity(getBaseUrl() + "/api/customers", request, String.class);
        assertThat(postResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        HttpEntity<NewAccountDto> accountCreationRequest = getDummyNewAccountRequest(1000);

        // second, create an account for him/her
        ResponseEntity<String> accountCreationResult = this.restTemplate.postForEntity(getBaseUrl() + "/api/customers/1/accounts", accountCreationRequest, String.class);
        assertThat(accountCreationResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Third, transfer between accounts
        HttpEntity<TransferDto> transferRequest = getDummyTransferRequest(1, 2, 300);

        this.restTemplate.put(getBaseUrl() + "/api/customers/1/transfer", transferRequest, String.class);

        // Verify the transfer
        ResponseEntity<String> getResult = this.restTemplate.getForEntity(getBaseUrl() + "/api/customers/1/accounts", String.class);
        assertThat(getResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        AccountDto[] accounts = objectMapper.readValue(getResult.getBody(), AccountDto[].class);
        assertThat(accounts.length).isEqualTo(2);
        assertThat(accounts[0].getId()).isEqualTo(1);
        assertThat(accounts[0].getBalance()).isEqualTo(BigDecimal.valueOf(1700));
        assertThat(accounts[1].getId()).isEqualTo(2);
        assertThat(accounts[1].getBalance()).isEqualTo(BigDecimal.valueOf(1300));
    }

    @Test
    public void transfer_NegativeAmount_ShouldReturnError() {
        HttpEntity<TransferDto> transferRequest = getDummyTransferRequest(1, 2, -100);

        HttpClientErrorException.BadRequest errorResponse = assertThrows(HttpClientErrorException.BadRequest.class, () ->
                this.restTemplate.put(getBaseUrl() + "/api/customers/1/transfer", transferRequest, String.class));

        assertEquals(errorResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
}
