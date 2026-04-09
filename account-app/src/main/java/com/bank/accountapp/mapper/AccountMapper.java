package com.bank.accountapp.mapper;

import com.bank.accountapp.dto.account.AccountResponseDto;
import com.bank.accountapp.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountMapper() {
    }

    public AccountResponseDto toAccountResponseDto(Account account) {
        return new AccountResponseDto(
                account.getUsername(),
                account.getName(),
                account.getBirthDate(),
                account.getBalance()
        );
    }
}
