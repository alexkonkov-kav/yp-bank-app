package com.bank.accountapp.dto.notification;

import java.math.BigDecimal;

public record TransferNotificationRequestDto(String fromUsername, String fromName, BigDecimal fromAmountOld, BigDecimal fromAmountNew,
                                             String toUsername, String toName, BigDecimal toAmountOld, BigDecimal toAmountNew,
                                             BigDecimal amount) {
}
