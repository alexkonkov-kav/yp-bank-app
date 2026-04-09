package com.bank.accountapp.dto.notification;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record EditAccountNotificationRequestDto(
        String username,
        String oldName,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDate oldBirthdate,
        String newName,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDate newBirthdate) {
}
