package com.bank.frontapp.controller;

import com.bank.frontapp.client.AccountClient;
import com.bank.frontapp.client.CashClient;
import com.bank.frontapp.client.TransferClient;
import com.bank.frontapp.dto.account.AccountDto;
import com.bank.frontapp.dto.account.AccountResponseDto;
import com.bank.frontapp.dto.account.EditAccountRequestDto;
import com.bank.frontapp.dto.transfer.TransferRequestDto;
import com.bank.frontapp.service.ModelErrorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class)
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountClient accountClient;

    @MockitoBean
    private CashClient cashClient;

    @MockitoBean
    private TransferClient transferClient;

    @MockitoBean
    private ModelErrorService modelErrorService;

    private AccountResponseDto myAccount;
    private AccountDto accResponseDto2;
    private AccountDto accResponseDto3;
    List<AccountDto> accounts;

    @BeforeEach
    void setUp() {
        myAccount = new AccountResponseDto("user1", "test user1",
                LocalDate.of(1990, 1, 1), new BigDecimal(100));
        accResponseDto2 = new AccountDto("user2", "test user2");
        accResponseDto3 = new AccountDto("user3", "test user3");
        accounts = List.of(accResponseDto2, accResponseDto3);
    }

    @Test
    void shouldReturnMainPageWithModel() throws Exception {
        when(accountClient.getMyAccount()).thenReturn(myAccount);
        when(accountClient.getAccountsForTransfer()).thenReturn(accounts);
        mockMvc.perform(get("/account")
                        .with(user("test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attribute("name", myAccount.name()))
                .andExpect(model().attribute("birthdate", myAccount.birthDate().toString()))
                .andExpect(model().attribute("sum", myAccount.balance()))
                .andExpect(model().attribute("accounts", accounts))
                .andExpect(model().attribute("info", "Успех!"));
        verify(accountClient).getMyAccount();
        verify(accountClient).getAccountsForTransfer();
    }

    @Test
    void shouldEditAccount_andReturnMainPage() throws Exception {
        when(accountClient.editAccount(any(EditAccountRequestDto.class))).thenReturn(myAccount);
        when(accountClient.getAccountsForTransfer()).thenReturn(accounts);
        mockMvc.perform(post("/account")
                        .with(user("test").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "user1")
                        .param("birthdate", "1990-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attribute("name", myAccount.name()))
                .andExpect(model().attribute("birthdate", myAccount.birthDate().toString()))
                .andExpect(model().attribute("sum", myAccount.balance()))
                .andExpect(model().attribute("accounts", accounts))
                .andExpect(model().attribute("info", "Данные изменены"));
        verify(accountClient).editAccount(any(EditAccountRequestDto.class));
        verify(accountClient).getAccountsForTransfer();
    }

    @Test
    void shouldTransfer_andReturnMainPage() throws Exception {
        when(transferClient.transfer(any(TransferRequestDto.class))).thenReturn(myAccount);
        when(accountClient.getAccountsForTransfer()).thenReturn(accounts);
        mockMvc.perform(post("/transfer")
                        .with(user("test").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("value", "100")
                        .param("login", "user2"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attribute("name", myAccount.name()))
                .andExpect(model().attribute("birthdate", myAccount.birthDate().toString()))
                .andExpect(model().attribute("sum", myAccount.balance()))
                .andExpect(model().attribute("accounts", accounts))
                .andExpect(model().attribute("info", "Перевод выполнен"));
        verify(transferClient).transfer(any(TransferRequestDto.class));
        verify(accountClient).getAccountsForTransfer();
    }
}
