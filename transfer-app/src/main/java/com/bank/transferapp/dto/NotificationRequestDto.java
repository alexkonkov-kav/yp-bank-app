package com.bank.transferapp.dto;

public record NotificationRequestDto(
        String fromUsername,
        Long fromId,
        Long toId,
        Integer amount) {
}
