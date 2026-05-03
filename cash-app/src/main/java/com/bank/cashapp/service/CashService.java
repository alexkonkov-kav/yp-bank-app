package com.bank.cashapp.service;

import com.bank.cashapp.client.AccountClient;
import com.bank.cashapp.dto.*;
import com.bank.cashapp.kafka.producer.NotificationProducer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CashService {

    private final AccountClient accountClient;
    private final NotificationProducer notificationProducer;

    public CashService(AccountClient accountClient,
                       NotificationProducer notificationProducer) {
        this.accountClient = accountClient;
        this.notificationProducer = notificationProducer;
    }

    public AccountResponseDto editCash(String username, EditCashRequestDto requestDto) {
        if (requestDto.getValue() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Сумма должна быть больше 0");
        }
        AccountIdResponseDto accountIdDto = accountClient.getAccountIdByUserName(username);
        AccountResponseDto accountDto = accountClient.editCash(
                new AccountIdEditCashRequestDto(accountIdDto.id(), requestDto.getValue(), requestDto.getAction()));
        notificationProducer.sendEditCashNotification(getNotificationRequestDto(username, accountDto, requestDto));
        return accountDto;
    }

    private NotificationRequestDto getNotificationRequestDto(String username, AccountResponseDto accountDto, EditCashRequestDto editCashDto) {
        return new NotificationRequestDto(username, accountDto.name(), editCashDto.getAction().name(), editCashDto.getValue(), accountDto.balance());
    }
}
