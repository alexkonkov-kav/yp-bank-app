package com.bank.transferapp.service;

import com.bank.transferapp.client.AccountClient;
import com.bank.transferapp.dto.AccountIdResponseDto;
import com.bank.transferapp.dto.AccountResponseDto;
import com.bank.transferapp.dto.AccountsTransferRequestDto;
import com.bank.transferapp.dto.TransferRequestDto;
import com.bank.transferapp.kafka.producer.NotificationProducer;
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
public class TransferServiceTest {

    @InjectMocks
    private TransferService transferService;

    @Mock
    private AccountClient accountClient;

    @Mock
    private NotificationProducer notificationProducer;

    @Test
    void shouldTransferSuccessfully() {
        String username = "user1";
        TransferRequestDto request = new TransferRequestDto();
        request.setLogin("user2");
        request.setValue(100);
        AccountIdResponseDto fromAccount = new AccountIdResponseDto(1L);
        AccountIdResponseDto toAccount = new AccountIdResponseDto(2L);
        AccountResponseDto response = new AccountResponseDto(
                "user1",
                "test user1",
                LocalDate.of(1990, 1, 1),
                new BigDecimal(1000)
        );
        when(accountClient.getAccountIdByUserName(username)).thenReturn(fromAccount);
        when(accountClient.getAccountIdByUserName(request.getLogin())).thenReturn(toAccount);
        when(accountClient.transfer(any(AccountsTransferRequestDto.class))).thenReturn(response);
        AccountResponseDto result = transferService.transfer(username, request);
        assertThat(result).isEqualTo(response);
        verify(accountClient).getAccountIdByUserName(username);
        verify(accountClient).getAccountIdByUserName(request.getLogin());
        verify(accountClient).transfer(any(AccountsTransferRequestDto.class));
        verify(notificationProducer).sendTransferNotification(any());
    }

    @Test
    void shouldThrowException_whenAmountLessOrEqualZero() {
        String username = "user1";
        TransferRequestDto request = new TransferRequestDto();
        request.setLogin("user2");
        request.setValue(0);
        assertThatThrownBy(() -> transferService.transfer(username, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Сумма должна быть больше 0");
        verifyNoInteractions(accountClient);
        verifyNoInteractions(notificationProducer);
    }
}
