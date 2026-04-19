package com.bank.accountapp.client;

import com.bank.accountapp.dto.notification.EditAccountNotificationRequestDto;
import com.bank.accountapp.dto.notification.TransferNotificationRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NotificationClient {

    private final WebClient notificationWebClient;

    public NotificationClient(WebClient notificationWebClient) {
        this.notificationWebClient = notificationWebClient;
    }

    public void sendEditAccountNotification(EditAccountNotificationRequestDto requestDto) {
        notificationWebClient
                .post()
                .uri("/account")
                .bodyValue(requestDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    //дублирование в Transfer-app есть похожий запрос
    public void sendTransferNotification(TransferNotificationRequestDto requestDto) {
        notificationWebClient
                .post()
                .uri("/transfer-account")
                .bodyValue(requestDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
