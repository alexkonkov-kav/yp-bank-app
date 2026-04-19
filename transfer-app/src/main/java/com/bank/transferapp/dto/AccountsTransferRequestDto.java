package com.bank.transferapp.dto;

public record AccountsTransferRequestDto(Long fromId, Long toId, Integer value) {
}
