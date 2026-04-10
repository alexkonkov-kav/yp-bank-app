package com.bank.accountapp.contract;

import com.bank.accountapp.client.NotificationClient;
import com.bank.accountapp.dto.notification.EditAccountNotificationRequestDto;
import com.bank.accountapp.dto.notification.TransferNotificationRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    void sendEditAccountNotificationTest() {
        assertDoesNotThrow(() ->
                notificationClient.sendEditAccountNotification(
                        new EditAccountNotificationRequestDto("user1", "oldTest", LocalDate.of(1990, 1, 1),
                                "newTest", LocalDate.of(1991, 1, 1))));
    }

    @Test
    void sendTransferNotificationTest() {
        assertDoesNotThrow(() ->
                notificationClient.sendTransferNotification(
                        new TransferNotificationRequestDto("user1", "fromName", new BigDecimal(10), new BigDecimal(20),
                                "user2", "toName", new BigDecimal(10), new BigDecimal(20), new BigDecimal(10))));
    }
}