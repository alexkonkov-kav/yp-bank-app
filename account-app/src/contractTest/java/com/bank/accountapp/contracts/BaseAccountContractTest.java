package com.bank.accountapp.contracts;

import com.bank.accountapp.dto.account.AccountIdResponseDto;
import com.bank.accountapp.dto.account.AccountResponseDto;
import com.bank.accountapp.service.AccountService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("contract-test")
public abstract class BaseAccountContractTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected AccountService accountService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        when(accountService.getIdAccountByUsername("user1"))
                .thenReturn(new AccountIdResponseDto(1L));

        when(accountService.transfer(any()))
                .thenReturn(new AccountResponseDto("user1", "test user", LocalDate.of(1990,1,1), new BigDecimal(1000)));

        when(accountService.editCash(any()))
                .thenReturn(new AccountResponseDto("user1", "test user", LocalDate.of(1990,1,1), new BigDecimal(1000)));
    }
}
