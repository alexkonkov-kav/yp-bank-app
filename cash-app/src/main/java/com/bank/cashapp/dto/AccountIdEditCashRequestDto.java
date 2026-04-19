package com.bank.cashapp.dto;

public record AccountIdEditCashRequestDto(Long accountId, Integer value, CashAction action) {
}
