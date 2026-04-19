package com.bank.frontapp.client;

import com.bank.frontapp.dto.account.AccountResponseDto;
import com.bank.frontapp.dto.cash.EditCashRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CashClient {

    private final WebClient gatewayWebClient;
    private final String cashBaseUrl;

    public CashClient(WebClient gatewayWebClient,
                      @Value("${bank.cash-gateway.base-url}") String cashBaseUrl) {
        this.gatewayWebClient = gatewayWebClient;
        this.cashBaseUrl = cashBaseUrl;
    }

    public AccountResponseDto editCash(EditCashRequestDto requestDto) {
        return gatewayWebClient
                .post()
                .uri(cashBaseUrl)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(AccountResponseDto.class)
                .block();
    }
}
