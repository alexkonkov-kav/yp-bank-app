package com.bank.frontapp.dto.account;

import java.time.LocalDate;

public record EditAccountRequestDto(String name, LocalDate birthdate) {
}
