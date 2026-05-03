package com.bank.notificationapp.kafka.consumer;

import com.bank.notificationapp.dto.TransferRequestDto;
import com.bank.notificationapp.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public TransferConsumer(NotificationService notificationService,
                            ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "transfer-events")
    public void editAccount(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            TransferRequestDto dto = objectMapper.readValue(
                    record.value(),
                    TransferRequestDto.class
            );
            notificationService.transferFromTransfer(dto);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения: ключ [{}], ошибка [{}]", record.key(), e.getMessage());
        }
    }
}
