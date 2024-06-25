package com.sajad.banking.BankingApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajad.banking.BankingApp.web.dto.account.NewAccountDto;
import com.sajad.banking.BankingApp.web.dto.account.TransferDto;
import com.sajad.banking.BankingApp.web.dto.customer.NewCustomerDto;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static com.sajad.banking.BankingApp.model.Customer.CustomerType.REAL;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Abstract class contains dependencies and some auxiliary methods!
 *
 * @author Sajad
 */
@SpringBootTest(classes = BankingAppApplication.class, webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
public abstract class TestBase {

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @LocalServerPort
    private Integer localServerPort;

    protected String getBaseUrl() {
        return "http://localhost:" + localServerPort;
    }

    protected HttpEntity<NewCustomerDto> getDummyNewCustomerRequest(int amount) {
        NewCustomerDto newCustomerDto = new NewCustomerDto("test", REAL, "lastName", "+98123", null, BigDecimal.valueOf(amount));
        return new HttpEntity<>(newCustomerDto, new HttpHeaders());
    }

    protected HttpEntity<NewAccountDto> getDummyNewAccountRequest(int amount) {
        NewAccountDto newAccountDto = new NewAccountDto();
        newAccountDto.setBalance(BigDecimal.valueOf(amount));
        return new HttpEntity<>(newAccountDto, new HttpHeaders());
    }

    protected HttpEntity<TransferDto> getDummyTransferRequest(int from, int to, int amount) {
        TransferDto transferDto = new TransferDto();
        transferDto.setFromAccountId(from);
        transferDto.setToAccountId(to);
        transferDto.setAmount(BigDecimal.valueOf(amount));

        return new HttpEntity<>(transferDto, new HttpHeaders());
    }
}
