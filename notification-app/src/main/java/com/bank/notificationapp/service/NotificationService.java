package com.bank.notificationapp.service;

import com.bank.notificationapp.dto.CashRequestDto;
import com.bank.notificationapp.dto.EditAccountRequestDto;
import com.bank.notificationapp.dto.TransferAccountRequestDto;
import com.bank.notificationapp.dto.TransferRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public NotificationService() {
    }

    public void editAccount(EditAccountRequestDto requestDto) {
        log.info(
                "Пользователь [{}]. Изменение аккаунта пользователя: имя [{} -> {}], дата рождения [{} -> {}]. Сервис Account.",
                requestDto.username(),
                requestDto.oldName(),
                requestDto.newName(),
                requestDto.oldBirthdate(),
                requestDto.newBirthdate()
        );
    }

    public void transferFromAccount(TransferAccountRequestDto requestDto) {
        log.info(
                "Пользователь [{}]. Сумма перевода [{}]. Перевод со счета аккаунта [{}:{}]:Баланс:[{} -> {}] - пользователю [{}:{}]:Баланс:[{} -> {}]. Сервис Account.",
                requestDto.fromUsername(),
                requestDto.amount().toString(),
                requestDto.fromUsername(),
                requestDto.fromName(),
                requestDto.fromAmountOld().toString(),
                requestDto.fromAmountNew().toString(),
                requestDto.toUsername(),
                requestDto.toName(),
                requestDto.toAmountOld().toString(),
                requestDto.toAmountNew().toString()
        );
    }

    public void transferFromTransfer(TransferRequestDto requestDto) {
        log.info(
                "Пользователь {}. Сумма перевода {}. Перевод со счета id аккаунта [{}] - пользователю с id [{}]. Сервис Transfer.",
                requestDto.fromUsername(),
                requestDto.amount().toString(),
                requestDto.fromId(),
                requestDto.toId()
        );
    }

    public void editCash(CashRequestDto requestDto) {
        log.info(
                "Пользователь [{}:{}]. Тип действия [{}]. Сумма [{}]. Баланс [{}]. Сервис Cash.",
                requestDto.username(),
                requestDto.name(),
                requestDto.typeAction(),
                requestDto.actionValue(),
                requestDto.afterValue()
        );
    }
}
