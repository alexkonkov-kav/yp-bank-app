package com.bank.cashapp.client;

import com.bank.cashapp.dto.AccountIdEditCashRequestDto;
import com.bank.cashapp.dto.AccountIdResponseDto;
import com.bank.cashapp.dto.AccountResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AccountClient {

    private final WebClient accountWebClient;
    private final String gatewayBaseUrl;

    public AccountClient(WebClient accountWebClient,
                         @Value("${bank.account-gateway.base-url}") String gatewayBaseUrl) {
        this.accountWebClient = accountWebClient;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    public AccountIdResponseDto getAccountIdByUserName(String userName) {
        return accountWebClient
                .get()
                .uri(gatewayBaseUrl + "/account/{username}", userName)
                .retrieve()
                .bodyToMono(AccountIdResponseDto.class)
                .block();
    }

    public AccountResponseDto editCash(AccountIdEditCashRequestDto requestDto) {
        return accountWebClient
                .post()
                .uri(gatewayBaseUrl + "/editCash")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(AccountResponseDto.class)
                .block();
    }
}
