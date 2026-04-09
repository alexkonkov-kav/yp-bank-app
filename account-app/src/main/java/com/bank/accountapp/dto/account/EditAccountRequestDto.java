package com.bank.accountapp.dto.account;

import java.time.LocalDate;

public record EditAccountRequestDto(String name, LocalDate birthdate) {
}
