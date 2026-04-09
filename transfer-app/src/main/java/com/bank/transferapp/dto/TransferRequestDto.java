package com.bank.transferapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TransferRequestDto {

    @NotNull(message = "Сумма обязательна")
    @Min(value = 1, message = "Сумма должна быть больше 0")
    private Integer value;

    @NotBlank(message = "Логин получателя обязателен")
    private String login;

    public TransferRequestDto() {
    }

    public @NotBlank(message = "Логин получателя обязателен") String getLogin() {
        return login;
    }

    public void setLogin(@NotBlank(message = "Логин получателя обязателен") String login) {
        this.login = login;
    }

    public @NotNull(message = "Сумма обязательна") @Min(value = 1, message = "Сумма должна быть больше 0") Integer getValue() {
        return value;
    }

    public void setValue(@NotNull(message = "Сумма обязательна") @Min(value = 1, message = "Сумма должна быть больше 0") Integer value) {
        this.value = value;
    }
}
