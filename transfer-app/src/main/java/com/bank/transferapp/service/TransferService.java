package com.bank.transferapp.service;

import com.bank.transferapp.client.AccountClient;
import com.bank.transferapp.dto.*;
import com.bank.transferapp.kafka.producer.NotificationProducer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransferService {

    private final AccountClient accountClient;
    private final NotificationProducer notificationProducer;

    public TransferService(AccountClient accountClient,
                           NotificationProducer notificationProducer) {
        this.accountClient = accountClient;
        this.notificationProducer = notificationProducer;
    }

    public AccountResponseDto transfer(String username, TransferRequestDto requestDto) {
        if (requestDto.getValue() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Сумма должна быть больше 0");
        }
        AccountIdResponseDto fromAccountIdDto = accountClient.getAccountIdByUserName(username);
        AccountIdResponseDto toAccountIdDto = accountClient.getAccountIdByUserName(requestDto.getLogin());
        AccountResponseDto accountDto = accountClient.transfer(new AccountsTransferRequestDto(fromAccountIdDto.id(), toAccountIdDto.id(), requestDto.getValue()));

        notificationProducer.sendTransferNotification(new NotificationRequestDto(username, fromAccountIdDto.id(), toAccountIdDto.id(), requestDto.getValue()));
        return accountDto;
    }
}
