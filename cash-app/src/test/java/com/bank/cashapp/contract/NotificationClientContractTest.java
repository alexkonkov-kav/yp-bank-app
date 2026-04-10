package com.bank.cashapp.contract;

import com.bank.cashapp.client.NotificationClient;
import com.bank.cashapp.dto.CashAction;
import com.bank.cashapp.dto.NotificationRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("contract-test")
@AutoConfigureStubRunner(
        ids = "com.bank:notification:+:stubs:8092",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class NotificationClientContractTest {

    @Autowired
    private NotificationClient notificationClient;

    @Test
    void sendEditCashNotificationTest() {
        assertDoesNotThrow(() ->
                notificationClient.sendEditCashNotification(
                        new NotificationRequestDto("user1", "name", CashAction.PUT.toString(), 10, new BigDecimal(10))));
    }
}