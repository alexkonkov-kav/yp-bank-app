package com.bank.accountapp.dto.transfer;

import java.math.BigDecimal;

public record TransferRequestDto(Long fromId, Long toId, BigDecimal value) {
}
