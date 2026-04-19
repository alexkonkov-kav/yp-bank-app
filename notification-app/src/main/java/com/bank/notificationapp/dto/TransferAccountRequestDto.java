package com.bank.notificationapp.dto;

import java.math.BigDecimal;

public record TransferAccountRequestDto(String fromUsername, String fromName, BigDecimal fromAmountOld, BigDecimal fromAmountNew,
                                        String toUsername, String toName, BigDecimal toAmountOld, BigDecimal toAmountNew,
                                        BigDecimal amount) {
}
