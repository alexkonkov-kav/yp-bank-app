package com.bank.accountapp.controller;

import com.bank.accountapp.dto.account.AccountIdResponseDto;
import com.bank.accountapp.dto.account.AccountResponseDto;
import com.bank.accountapp.dto.account.EditAccountRequestDto;
import com.bank.accountapp.dto.cash.EditCashRequestDto;
import com.bank.accountapp.dto.transfer.TransferRequestDto;
import com.bank.accountapp.service.AccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account")
    @PreAuthorize("hasRole('USER')")
    public AccountResponseDto getAccount(JwtAuthenticationToken jwt) {
        String username = jwt.getToken().getClaimAsString("preferred_username");
        return accountService.getAccountByUsername(username);
    }

    @GetMapping("/accounts")
    @PreAuthorize("hasRole('USER')")
    public List<AccountResponseDto> getAccounts(JwtAuthenticationToken jwt) {
        String username = jwt.getToken().getClaimAsString("preferred_username");
        return accountService.getAccountsForTransfer(username);
    }

    @GetMapping("/account/{username}")
    @PreAuthorize("hasRole('SERVICE')")
    public AccountIdResponseDto getIdAccountByUsername(@PathVariable String username) {
        return accountService.getIdAccountByUsername(username);
    }

    @PostMapping("/account")
    @PreAuthorize("hasRole('USER') && hasAuthority('account.write')")
    public AccountResponseDto editAccount(JwtAuthenticationToken jwt, @RequestBody EditAccountRequestDto dto) {
        String username = jwt.getToken().getClaimAsString("preferred_username");
        return accountService.editAccount(username, dto);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('SERVICE') && hasAuthority('account.write')")
    public AccountResponseDto transfer(@RequestBody TransferRequestDto dto) {
        return accountService.transfer(dto);
    }

    @PostMapping("/editCash")
    @PreAuthorize("hasRole('SERVICE') && hasAuthority('account.write')")
    public AccountResponseDto editCash(@RequestBody EditCashRequestDto requestDto) {
        return accountService.editCash(requestDto);
    }
}