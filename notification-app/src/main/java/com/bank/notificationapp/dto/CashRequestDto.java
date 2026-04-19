package com.bank.notificationapp.dto;

import java.math.BigDecimal;

public record CashRequestDto(
        String username,
        String name,
        String typeAction,
        Integer actionValue,
        BigDecimal afterValue) {
}
