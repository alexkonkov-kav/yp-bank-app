package com.bank.cashapp.dto;

import java.math.BigDecimal;

public record NotificationRequestDto(
        String username,
        String name,
        String typeAction,
        Integer actionValue,
        BigDecimal afterValue) {
}
