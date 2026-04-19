package com.bank.frontapp.client;

import com.bank.frontapp.dto.account.AccountResponseDto;
import com.bank.frontapp.dto.transfer.TransferRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TransferClient {

    private final WebClient gatewayWebClient;
    private final String gatewayBaseUrl;

    public TransferClient(WebClient gatewayWebClient,
                          @Value("${bank.transfer-gateway.base-url}") String gatewayBaseUrl) {
        this.gatewayWebClient = gatewayWebClient;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    public AccountResponseDto transfer(TransferRequestDto requestDto) {
        return gatewayWebClient
                .post()
                .uri(gatewayBaseUrl)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(AccountResponseDto.class)
                .block();
    }
}
