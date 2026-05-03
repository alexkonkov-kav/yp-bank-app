package com.bank.notificationapp.kafka.consumer;

import com.bank.notificationapp.dto.EditAccountRequestDto;
import com.bank.notificationapp.dto.TransferAccountRequestDto;
import com.bank.notificationapp.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public AccountConsumer(NotificationService notificationService,
                           ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "account-events")
    public void editAccount(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            EditAccountRequestDto dto = objectMapper.readValue(
                    record.value(),
                    EditAccountRequestDto.class
            );
            notificationService.editAccount(dto);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logError(record, e);
        }
    }

    @KafkaListener(topics = "account-events")
    public void transferAccount(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            TransferAccountRequestDto dto = objectMapper.readValue(
                    record.value(),
                    TransferAccountRequestDto.class
            );
            notificationService.transferFromAccount(dto);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logError(record, e);
        }
    }

    private void logError(ConsumerRecord<String, String> record, Exception e) {
        log.error("Ошибка обработки сообщения: ключ [{}], ошибка [{}]", record.key(), e.getMessage());
    }
}