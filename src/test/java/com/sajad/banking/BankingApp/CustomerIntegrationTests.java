package com.sajad.banking.BankingApp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sajad.banking.BankingApp.web.dto.customer.CustomerDetailDto;
import com.sajad.banking.BankingApp.web.dto.customer.CustomerListDto;
import com.sajad.banking.BankingApp.web.dto.customer.NewCustomerDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.HashMap;

import static com.sajad.banking.BankingApp.model.Customer.CustomerType.LEGAL;
import static com.sajad.banking.BankingApp.model.Customer.CustomerType.REAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@linkplain com.sajad.banking.BankingApp.web.controller.CustomerController}
 * <p>
 * We don't mock any parties, or modules, data are ready going to persist in an embedded H2 db.
 * The context gets dirty after each test's execution to avoid interfering other test cases.
 *
 * @author Sajad
 */
public class CustomerIntegrationTests extends TestBase {

    @Test
    public void listCustomer_NoCustomer_ShouldReturnEmptyList() throws JsonProcessingException {
        ResponseEntity<String> getResult = this.restTemplate.getForEntity(getBaseUrl() + "/api/customers", String.class);
        assertThat(getResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Evaluate doctor's appointment list
        CustomerListDto[] customers = objectMapper.readValue(getResult.getBody(), CustomerListDto[].class);
        assertThat(customers).isEmpty();
    }

    @Test
    public void findCustomer_CustomerExists_ShouldReturnCustomerDetails() throws JsonProcessingException {
        // First, create a new customer
        NewCustomerDto newCustomerDto = new NewCustomerDto("test", REAL, "lastName", "+98123", null, BigDecimal.TEN);
        HttpEntity<NewCustomerDto> request = new HttpEntity<>(newCustomerDto, new HttpHeaders());

        ResponseEntity<String> postResult = this.restTemplate.postForEntity(getBaseUrl() + "/api/customers", request, String.class);
        assertThat(postResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Second, find the customer
        ResponseEntity<String> findResult = this.restTemplate.getForEntity(getBaseUrl() + "/api/customers/1", String.class);
        assertThat(findResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Evaluate doctor's appointment list
        CustomerDetailDto[] customerDetailDtoArray = objectMapper.readValue(findResult.getBody(), CustomerDetailDto[].class);
        assertThat(customerDetailDtoArray.length).isEqualTo(1);

        // Evaluate customer details
        CustomerDetailDto detailDto = customerDetailDtoArray[0];
        assertThat(detailDto.getId()).isEqualTo(1);
        assertThat(detailDto.getName()).isEqualTo(newCustomerDto.getName());
        assertThat(detailDto.getType()).isEqualTo(newCustomerDto.getType());
        assertThat(detailDto.getPhoneNumber()).isEqualTo(newCustomerDto.getPhoneNumber());
        assertThat(detailDto.getLastName()).isEqualTo(newCustomerDto.getLastName());
    }

    /**
     * Attempt to create a new customer should be successful.
     */
    @Test
    public void createRealCustomer_validInput_ShouldBeSuccessful() throws JsonProcessingException {
        // First, create a new customer
        HttpEntity<NewCustomerDto> request = getDummyNewCustomerRequest(10);

        ResponseEntity<String> postResult = this.restTemplate.postForEntity(getBaseUrl() + "/api/customers", request, String.class);
        assertThat(postResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Assert the record is persisted
        ResponseEntity<String> getResult = this.restTemplate.getForEntity(getBaseUrl() + "/api/customers", String.class);
        assertThat(getResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Evaluate doctor's appointment list
        CustomerListDto[] customers = objectMapper.readValue(getResult.getBody(), CustomerListDto[].class);
        assertThat(customers.length).isEqualTo(1);
    }

    @Test
    public void createLegalCustomer_validInput_ShouldBeSuccessful() throws JsonProcessingException {
        // First, create a new legal customer
        NewCustomerDto newCustomerDto = new NewCustomerDto("test", LEGAL, null, "+98123", "+98123", BigDecimal.TEN);
        HttpEntity<NewCustomerDto> request = new HttpEntity<>(newCustomerDto, new HttpHeaders());

        ResponseEntity<String> postResult = this.restTemplate.postForEntity(getBaseUrl() + "/api/customers", request, String.class);
        assertThat(postResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Second, find the LEGAL customer
        ResponseEntity<String> findResult = this.restTemplate.getForEntity(getBaseUrl() + "/api/customers/1", String.class);
        assertThat(findResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Evaluate detail DTO
        CustomerDetailDto[] customerDetailDtoArray = objectMapper.readValue(findResult.getBody(), CustomerDetailDto[].class);
        assertThat(customerDetailDtoArray.length).isEqualTo(1);

        CustomerDetailDto detailDto = customerDetailDtoArray[0];
        assertThat(detailDto.getId()).isEqualTo(1);
        assertThat(detailDto.getName()).isEqualTo(newCustomerDto.getName());
        assertThat(detailDto.getType()).isEqualTo(newCustomerDto.getType());
        assertThat(detailDto.getPhoneNumber()).isEqualTo(newCustomerDto.getPhoneNumber());
        assertThat(detailDto.getLastName()).isEqualTo(newCustomerDto.getLastName());
    }

    @Test
    public void createCustomer_duplicateCustomer_ShouldThrowError() {
        HttpEntity<NewCustomerDto> request = getDummyNewCustomerRequest(100);

        ResponseEntity<String> result = this.restTemplate.postForEntity(getBaseUrl() + "/api/customers", request, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Try to create again!
        HttpClientErrorException.BadRequest response = assertThrows(HttpClientErrorException.BadRequest.class, () ->
                this.restTemplate.postForEntity(getBaseUrl() + "/api/customers", request, String.class));

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNotNull(response.getMessage());
        assertEquals(response.getMessage(), "400 : \"Customer already registered with this phone number!\"");
    }

    @Test
    public void deleteCustomer_validCondition_ShouldBeSuccessful() throws JsonProcessingException {
        // First, create a customer with zero balance
        HttpEntity<NewCustomerDto> request = getDummyNewCustomerRequest(0);

        ResponseEntity<String> result = this.restTemplate.postForEntity(getBaseUrl() + "/api/customers", request, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Second, delete one of them
        this.restTemplate.delete(getBaseUrl() + "/api/customers/1", new HashMap<String, String>());

        // Verify the there is no customer!
        ResponseEntity<String> getResult = this.restTemplate.getForEntity(getBaseUrl() + "/api/customers", String.class);
        assertThat(getResult.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

        // Verify emptiness of the list!
        CustomerListDto[] customers = objectMapper.readValue(getResult.getBody(), CustomerListDto[].class);
        assertThat(customers).isEmpty();
    }

    @Test
    public void deleteCustomer_NotExists_ShouldThrowError() {
        HttpClientErrorException.NotFound response = assertThrows(HttpClientErrorException.NotFound.class, () ->
                this.restTemplate.delete(getBaseUrl() + "/api/customers/1", new HashMap<String, String>()));
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }
}
