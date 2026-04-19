package com.bank.notificationapp.dto;

import java.time.LocalDate;

public record EditAccountRequestDto(String username, String oldName, LocalDate oldBirthdate,
                                    String newName, LocalDate newBirthdate) {
}
