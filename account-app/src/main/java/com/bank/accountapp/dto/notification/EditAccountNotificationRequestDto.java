package com.bank.accountapp.dto.notification;

import java.time.LocalDate;

public record EditAccountNotificationRequestDto(String username, String oldName, LocalDate oldBirthdate,
                                                String newName, LocalDate newBirthdate) {
}
