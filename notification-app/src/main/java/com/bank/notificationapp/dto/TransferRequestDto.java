package com.bank.notificationapp.dto;

public record TransferRequestDto(
        String fromUsername,
        Long fromId,
        Long toId,
        Integer amount) {
}
