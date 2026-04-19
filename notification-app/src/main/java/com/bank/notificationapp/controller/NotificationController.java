package com.bank.notificationapp.controller;

import com.bank.notificationapp.dto.CashRequestDto;
import com.bank.notificationapp.dto.EditAccountRequestDto;
import com.bank.notificationapp.dto.TransferAccountRequestDto;
import com.bank.notificationapp.dto.TransferRequestDto;
import com.bank.notificationapp.service.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/account")
    @PreAuthorize("hasRole('SERVICE') && hasAuthority('account.write')")
    public void editAccount(@RequestBody EditAccountRequestDto requestDto) {
        notificationService.editAccount(requestDto);
    }

    @PostMapping("/transfer-account")
    @PreAuthorize("hasRole('SERVICE') && hasAuthority('account.write')")
    public void transferAccount(JwtAuthenticationToken jwt, @RequestBody TransferAccountRequestDto requestDto) {
        String username = jwt.getToken().getClaimAsString("preferred_username");
        notificationService.transferFromAccount(username, requestDto);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('SERVICE') && hasAuthority('transfer.write')")
    public void transfer(@RequestBody TransferRequestDto requestDto) {
        notificationService.transferFromTransfer(requestDto);
    }

    @PostMapping("/cash")
    @PreAuthorize("hasRole('SERVICE') && hasAuthority('cash.write')")
    public void editCash(@RequestBody CashRequestDto requestDto) {
        notificationService.editCash(requestDto);
    }
}
