package com.bank.cashapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class EditCashRequestDto {

    @NotNull(message = "Сумма обязательна")
    @Min(value = 1, message = "Сумма должна быть больше 0")
    private Integer value;

    @NotNull(message = "Тип операции обязателен")
    private CashAction action;

    public EditCashRequestDto() {
    }

    public @NotNull(message = "Сумма обязательна") @Min(value = 1, message = "Сумма должна быть больше 0") Integer getValue() {
        return value;
    }

    public void setValue(@NotNull(message = "Сумма обязательна") @Min(value = 1, message = "Сумма должна быть больше 0") Integer value) {
        this.value = value;
    }

    public @NotNull(message = "Тип операции обязателен") CashAction getAction() {
        return action;
    }

    public void setAction(@NotNull(message = "Тип операции обязателен") CashAction action) {
        this.action = action;
    }
}
