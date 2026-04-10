package com.bank.cashapp.contract;

import com.bank.cashapp.client.AccountClient;
import com.bank.cashapp.dto.AccountIdEditCashRequestDto;
import com.bank.cashapp.dto.AccountIdResponseDto;
import com.bank.cashapp.dto.AccountResponseDto;
import com.bank.cashapp.dto.CashAction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("contract-test")
@AutoConfigureStubRunner(
        ids = "com.bank:account:+:stubs:8091",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class AccountsClientContractTest {

    @Autowired
    private AccountClient accountClient;

    @Test
    void getAccountIdByUserNameTest() {
        AccountIdResponseDto result = accountClient.getAccountIdByUserName("user1");
        assertNotNull(result);
    }

    @Test
    void editCashTest() {
        AccountResponseDto result = accountClient.editCash(new AccountIdEditCashRequestDto(1L, 2, CashAction.PUT));
        assertNotNull(result);
    }
}