package com.bank.accountapp.dto.cash;

public record EditCashRequestDto(Long accountId, Integer value, CashAction action) {
}
