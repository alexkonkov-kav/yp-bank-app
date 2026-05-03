package com.bank.transferapp.kafka.producer;

import com.bank.transferapp.dto.NotificationRequestDto;
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

    public void sendTransferNotification(NotificationRequestDto requestDto) {
        try {
            log.info("Notification-app. Sending message to kafka");
            String message = objectMapper.writeValueAsString(requestDto);
            kafkaTemplate.send("transfer-events",
                    "Ключ: " + LocalDateTime.now(),
                    message
            );
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации NotificationRequestDto в JSON", e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка отправки NotificationRequestDto ", e);
        }
    }
}
