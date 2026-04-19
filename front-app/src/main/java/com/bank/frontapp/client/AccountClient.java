package com.bank.frontapp.client;

import com.bank.frontapp.dto.account.AccountDto;
import com.bank.frontapp.dto.account.AccountResponseDto;
import com.bank.frontapp.dto.account.EditAccountRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class AccountClient {

    private final WebClient gatewayWebClient;
    private final String gatewayBaseUrl;

    public AccountClient(WebClient gatewayWebClient,
                         @Value("${bank.account-gateway.base-url}") String gatewayBaseUrl) {
        this.gatewayWebClient = gatewayWebClient;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    public AccountResponseDto getMyAccount() {
        return gatewayWebClient
                .get()
                .uri(gatewayBaseUrl + "/account")
                .retrieve()
                .bodyToMono(AccountResponseDto.class)
                .block();
    }

    public List<AccountDto> getAccountsForTransfer() {
        return gatewayWebClient
                .get()
                .uri(gatewayBaseUrl + "/accounts")
                .retrieve()
                .bodyToFlux(AccountDto.class)
                .collectList()
                .block();
    }

    public AccountResponseDto editAccount(EditAccountRequestDto requestDto) {
        return gatewayWebClient
                .post()
                .uri(gatewayBaseUrl + "/account")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(AccountResponseDto.class)
                .block();
    }
}
