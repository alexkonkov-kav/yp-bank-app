package com.bank.notificationapp.contracts;

import com.bank.notificationapp.service.NotificationService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("contract-test")
public abstract class BaseNotificationContractTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected NotificationService notificationService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        doNothing().when(notificationService).editAccount(any());
        doNothing().when(notificationService).transferFromAccount(any(), any());
        doNothing().when(notificationService).transferFromTransfer(any());
        doNothing().when(notificationService).editCash(any());
    }
}
