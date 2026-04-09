package com.bank.cashapp.controller;

import com.bank.cashapp.dto.AccountResponseDto;
import com.bank.cashapp.dto.EditCashRequestDto;
import com.bank.cashapp.service.CashService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CashController {

    private final CashService cashService;

    public CashController(CashService cashService) {
        this.cashService = cashService;
    }

    @PostMapping("/cash")
    @PreAuthorize("hasRole('USER') && hasAuthority('cash.write')")
    public AccountResponseDto editCash(JwtAuthenticationToken jwt, @RequestBody EditCashRequestDto request) {
        String username = jwt.getToken().getClaimAsString("preferred_username");
        return cashService.editCash(username, request);
    }
}
