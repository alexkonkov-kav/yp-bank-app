package com.bank.cashapp.client;

import com.bank.cashapp.dto.NotificationRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NotificationClient {

    private final WebClient notificationWebClient;
    private final String gatewayBaseUrl;

    public NotificationClient(WebClient notificationWebClient,
                              @Value("${bank.notification-gateway.base-url}") String gatewayBaseUrl) {

        this.notificationWebClient = notificationWebClient;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    public void sendEditCashNotification(NotificationRequestDto requestDto) {
        notificationWebClient
                .post()
                .uri(gatewayBaseUrl + "/cash")
                .bodyValue(requestDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
