package com.bank.frontapp.controller;

import com.bank.frontapp.client.AccountClient;
import com.bank.frontapp.client.CashClient;
import com.bank.frontapp.client.TransferClient;
import com.bank.frontapp.dto.account.AccountDto;
import com.bank.frontapp.dto.account.AccountResponseDto;
import com.bank.frontapp.dto.account.EditAccountRequestDto;
import com.bank.frontapp.dto.cash.EditCashRequestDto;
import com.bank.frontapp.dto.transfer.TransferRequestDto;
import com.bank.frontapp.service.ModelErrorService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Контроллер main.html.
 * <p>
 * Используемая модель для main.html:
 * model.addAttribute("name", name);
 * model.addAttribute("birthdate", birthdate.format(DateTimeFormatter.ISO_DATE));
 * model.addAttribute("sum", sum);
 * model.addAttribute("accounts", accounts);
 * model.addAttribute("errors", errors);
 * model.addAttribute("info", info);
 * <p>
 * Поля модели:
 * name - Фамилия Имя текущего пользователя, String (обязательное)
 * birthdate - дата рождения текущего пользователя, String в формате 'YYYY-MM-DD' (обязательное)
 * sum - сумма на счету текущего пользователя, Integer (обязательное)
 * accounts - список аккаунтов, которым можно перевести деньги, List<AccountDto> (обязательное)
 * errors - список ошибок после выполнения действий, List<String> (не обязательное)
 * info - строка успешности после выполнения действия, String (не обязательное)
 * <p>
 * С примерами использования можно ознакомиться в тестовом классе заглушке AccountStub
 */
@Controller
@Validated
public class MainController {

    private final AccountClient accountClient;
    private final CashClient cashClient;
    private final TransferClient transferClient;
    private final ModelErrorService modelErrorService;

    public MainController(AccountClient accountClient,
                          CashClient cashClient,
                          TransferClient transferClient,
                          ModelErrorService modelErrorService) {
        this.accountClient = accountClient;
        this.cashClient = cashClient;
        this.transferClient = transferClient;
        this.modelErrorService = modelErrorService;
    }

    /**
     * GET /.
     * Редирект на GET /account
     */
    @GetMapping
    public String index() {
        return "redirect:/account";
    }

    /**
     * GET /account.
     * Что нужно сделать:
     * 1. Сходить в сервис accounts через Gateway API для получения данных аккаунта по REST
     * 2. Заполнить модель main.html полученными из ответа данными
     * 3. Текущего пользователя можно получить из контекста Security
     */
    @GetMapping("/account")
    public String getAccount(Model model) {
        AccountResponseDto myAccount = accountClient.getMyAccount();
        List<AccountDto> accounts = accountClient.getAccountsForTransfer();
        setDataToModel(model, myAccount, accounts, "Успех!");

        return "main";
    }

    /**
     * POST /account.
     * Что нужно сделать:
     * 1. Сходить в сервис accounts через Gateway API для изменения данных текущего пользователя по REST
     * 2. Заполнить модель main.html полученными из ответа данными
     * 3. Текущего пользователя можно получить из контекста Security
     * <p>
     * Изменяемые данные:
     * 1. name - Фамилия Имя
     * 2. birthdate - дата рождения в формате YYYY-DD-MM
     */
    @PostMapping("/account")
    public String editAccount(
            @ModelAttribute EditAccountRequestDto request,
            Model model
    ) {
        if (request.name().isBlank() && request.birthdate() == null) {
            model.addAttribute("errors", "Данные не изменены");
            return "main";
        }

        AccountResponseDto editAccount = accountClient.editAccount(request);
        List<AccountDto> accounts = accountClient.getAccountsForTransfer();
        setDataToModel(model, editAccount, accounts, "Данные изменены");

        return "main";
    }

    /**
     * POST /cash.
     * Что нужно сделать:
     * 1. Сходить в сервис cash через Gateway API для снятия/пополнения счета текущего аккаунта по REST
     * 2. Заполнить модель main.html полученными из ответа данными
     * 3. Текущего пользователя можно получить из контекста Security
     * <p>
     * Параметры:
     * 1. value - сумма списания
     * 2. action - GET (снять), PUT (пополнить)
     */
    @PostMapping("/cash")
    public String editCash(
            @Valid @ModelAttribute EditCashRequestDto request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", modelErrorService.getBindingMessages(bindingResult));
            return "main";
        }

        AccountResponseDto myAccount = cashClient.editCash(request);
        List<AccountDto> accounts = accountClient.getAccountsForTransfer();
        setDataToModel(model, myAccount, accounts, "Операция выполнена");

        return "main";
    }

    /**
     * POST /transfer.
     * Что нужно сделать:
     * 1. Сходить в сервис accounts через Gateway API для перевода со счета текущего аккаунта на счет другого аккаунта по REST
     * 2. Заполнить модель main.html полученными из ответа данными
     * 3. Текущего пользователя можно получить из контекста Security
     * <p>
     * Параметры:
     * 1. value - сумма списания
     * 2. login - логин пользователя получателя
     */
    @PostMapping("/transfer")
    public String transfer(
            @Valid @ModelAttribute TransferRequestDto request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", modelErrorService.getBindingMessages(bindingResult));
            return "main";
        }

        AccountResponseDto myAccount = transferClient.transfer(request);
        List<AccountDto> accounts = accountClient.getAccountsForTransfer();
        setDataToModel(model, myAccount, accounts, "Перевод выполнен");

        return "main";
    }

    @ExceptionHandler(WebClientResponseException.class)
    public String handleWebClientResponseException(
            WebClientResponseException exception,
            Model model,
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        if (exception.getStatusCode().is4xxClientError()) {
            String body = exception.getResponseBodyAsString();
            model.addAttribute("errors",
                    !body.isBlank() ? body : "Ошибка запроса:  " + exception.getStatusCode().value()
            );
        } else {
            model.addAttribute("errors", "Сервис временно недоступен");
        }
        if (oidcUser != null) {
            model.addAttribute("username", oidcUser.getPreferredUsername());
        }
        return "main";
    }

    private void setDataToModel(Model model, AccountResponseDto myAccount, List<AccountDto> accounts, String info) {
        model.addAttribute("name", myAccount.name());
        model.addAttribute("birthdate", myAccount.birthDate().format(DateTimeFormatter.ISO_DATE));
        model.addAttribute("sum", myAccount.balance());
        model.addAttribute("accounts", accounts);
        model.addAttribute("info", info);
    }
}
