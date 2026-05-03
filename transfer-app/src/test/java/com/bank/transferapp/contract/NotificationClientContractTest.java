//package com.bank.transferapp.contract;
//
//import com.bank.transferapp.client.NotificationClient;
//import com.bank.transferapp.dto.NotificationRequestDto;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
//import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("contract-test")
//@AutoConfigureStubRunner(
//        ids = "com.bank:notification:+:stubs:8092",
//        stubsMode = StubRunnerProperties.StubsMode.LOCAL
//)
//public class NotificationClientContractTest {
//
//    @Autowired
//    private NotificationClient notificationClient;
//
//    @Test
//    void sendTransferNotificationTest() {
//        assertDoesNotThrow(() ->
//                notificationClient.sendTransferNotification(
//                        new NotificationRequestDto("user1", 1L, 2L, 10)));
//    }
//}