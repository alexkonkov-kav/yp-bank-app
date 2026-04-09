package com.bank.transferapp.controller;

import com.bank.transferapp.dto.AccountResponseDto;
import com.bank.transferapp.dto.TransferRequestDto;
import com.bank.transferapp.service.TransferService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER') && hasAuthority('transfer.write')")
    public AccountResponseDto transfer(JwtAuthenticationToken jwt, @RequestBody TransferRequestDto request) {
        String username = jwt.getToken().getClaimAsString("preferred_username");
        return transferService.transfer(username, request);
    }
}