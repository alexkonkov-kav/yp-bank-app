package com.bank.cashapp.service;

import com.bank.cashapp.client.AccountClient;
import com.bank.cashapp.client.NotificationClient;
import com.bank.cashapp.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CashServiceTest {

    @InjectMocks
    private CashService cashService;

    @Mock
    private AccountClient accountClient;

    @Mock
    private NotificationClient notificationClient;

    @Test
    void shouldEditCashSuccessfully() {
        String username = "user1";
        EditCashRequestDto request = new EditCashRequestDto();
        request.setValue(100);
        request.setAction(CashAction.PUT);
        AccountIdResponseDto accountId = new AccountIdResponseDto(1L);
        AccountResponseDto accountResponse = new AccountResponseDto(
                username,
                "test user1",
                LocalDate.of(1990, 1, 1),
                new BigDecimal(200)
        );
        when(accountClient.getAccountIdByUserName(username)).thenReturn(accountId);
        when(accountClient.editCash(any(AccountIdEditCashRequestDto.class))).thenReturn(accountResponse);
        AccountResponseDto result = cashService.editCash(username, request);

        assertThat(result).isEqualTo(accountResponse);
        verify(accountClient).getAccountIdByUserName(username);
        verify(accountClient).editCash(any(AccountIdEditCashRequestDto.class));
        verify(notificationClient).sendEditCashNotification(any());
    }

    @Test
    void shouldThrowException_whenValueLessOrEqualZero() {
        String username = "user1";
        EditCashRequestDto request = new EditCashRequestDto();
        request.setValue(0);
        request.setAction(CashAction.PUT);
        assertThatThrownBy(() -> cashService.editCash(username, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Сумма должна быть больше 0");
        verifyNoInteractions(accountClient);
        verifyNoInteractions(notificationClient);
    }
}
