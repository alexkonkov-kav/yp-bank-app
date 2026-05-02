package com.bank.accountapp.kafka.producer;

import com.bank.accountapp.dto.notification.EditAccountNotificationRequestDto;
import com.bank.accountapp.dto.notification.TransferNotificationRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class NotificationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public NotificationProducer(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendEditAccountNotification(EditAccountNotificationRequestDto requestDto) {
        try {
            logInfo();
            String message = objectMapper.writeValueAsString(requestDto);
            kafkaTemplate.send("account-events",
                    "Ключ: " + LocalDateTime.now(),
                    message
            );
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации EditAccountNotificationRequestDto в JSON", e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка отправки EditAccountNotificationRequestDto ", e);
        }
    }

    public void sendTransferNotification(TransferNotificationRequestDto requestDto) {
        try {
            logInfo();
            String message = objectMapper.writeValueAsString(requestDto);
            kafkaTemplate.send("account-events",
                    "Ключ: " + LocalDateTime.now(),
                    message
            );
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации TransferNotificationRequestDto в JSON", e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка отправки TransferNotificationRequestDto ", e);
        }
    }

    private void logInfo() {
        log.info("Account-app. Sending message to kafka");
    }
}
