package com.bank.accountapp.dto.account;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountResponseDto(
        String login,
        String name,
        LocalDate birthDate,
        BigDecimal balance) {
}
