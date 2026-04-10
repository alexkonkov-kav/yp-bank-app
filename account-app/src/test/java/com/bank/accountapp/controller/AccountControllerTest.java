package com.bank.accountapp.controller;

import com.bank.accountapp.config.SecurityConfig;
import com.bank.accountapp.dto.account.AccountIdResponseDto;
import com.bank.accountapp.dto.account.AccountResponseDto;
import com.bank.accountapp.dto.account.EditAccountRequestDto;
import com.bank.accountapp.dto.cash.EditCashRequestDto;
import com.bank.accountapp.dto.transfer.TransferRequestDto;
import com.bank.accountapp.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(SecurityConfig.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    private static final String BASE_URL = "/accounts";
    private AccountResponseDto responseDto;
    private AccountResponseDto responseDto2;
    private AccountResponseDto responseDto3;

    @BeforeEach
    void setUp() {
        responseDto = new AccountResponseDto("user1", "test user1",
                LocalDate.of(1990, 1, 1), new BigDecimal(100));
        responseDto2 = new AccountResponseDto("user2", "test user2",
                LocalDate.of(1991, 1, 1), new BigDecimal(150));
        responseDto3 = new AccountResponseDto("user3", "test user3",
                LocalDate.of(1992, 1, 1), new BigDecimal(200));
    }

    @Test
    void shouldReturnAccount() throws Exception {
        String username = "user1";
        when(accountService.getAccountByUsername(username)).thenReturn(responseDto);

        mockMvc.perform(get(BASE_URL + "/account")
                        .with(jwt().jwt(jwt -> jwt.claim("preferred_username", username))
                                .authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.login").value(responseDto.login()))
                .andExpect(jsonPath("$.name").value(responseDto.name()))
                .andExpect(jsonPath("$.birthDate").value(responseDto.birthDate().toString()))
                .andExpect(jsonPath("$.balance").value(responseDto.balance()));

        verify(accountService).getAccountByUsername(username);
    }

    @Test
    void shouldReturnForbidden_whenNoRole() throws Exception {
        mockMvc.perform(get(BASE_URL + "/account")
                        .with(jwt().jwt(jwt -> jwt.claim("preferred_username", "user1"))
                                .authorities(() -> "ROLE_NOT_USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorized_whenNoJwt() throws Exception {
        mockMvc.perform(get(BASE_URL + "/account"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnAccountsList() throws Exception {
        String username = "user1";
        List<AccountResponseDto> response = List.of(responseDto2, responseDto3);
        when(accountService.getAccountsForTransfer(username)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/accounts")
                        .with(jwt().jwt(jwt -> jwt.claim("preferred_username", username))
                                .authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].login").value(responseDto2.login()))
                .andExpect(jsonPath("$[0].name").value(responseDto2.name()))
                .andExpect(jsonPath("$[0].birthDate").value(responseDto2.birthDate().toString()))
                .andExpect(jsonPath("$[0].balance").value(responseDto2.balance()))
                .andExpect(jsonPath("$[1].login").value(responseDto3.login()))
                .andExpect(jsonPath("$[1].name").value(responseDto3.name()))
                .andExpect(jsonPath("$[1].birthDate").value(responseDto3.birthDate().toString()))
                .andExpect(jsonPath("$[1].balance").value(responseDto3.balance()));
        verify(accountService).getAccountsForTransfer(username);
    }

    @Test
    void shouldReturnAccountId() throws Exception {
        String username = "user1";
        AccountIdResponseDto responseIdDto = new AccountIdResponseDto(1L);
        when(accountService.getIdAccountByUsername(username)).thenReturn(responseIdDto);
        mockMvc.perform(get(BASE_URL + "/account/{username}", username)
                        .with(jwt()
                                .authorities(() -> "ROLE_SERVICE")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1));
        verify(accountService).getIdAccountByUsername(username);
    }

    @Test
    void shouldEditAccount() throws Exception {
        String username = "user1";
        EditAccountRequestDto editRequestDto = new EditAccountRequestDto("newName", LocalDate.of(2000, 1, 1));
        AccountResponseDto responseAccDto = new AccountResponseDto(username, editRequestDto.name(), editRequestDto.birthdate(), new BigDecimal(100));
        when(accountService.editAccount(eq(username), any(EditAccountRequestDto.class))).thenReturn(responseAccDto);
        mockMvc.perform(post(BASE_URL + "/account")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("preferred_username", username))
                                .authorities(
                                        () -> "ROLE_USER",
                                        () -> "account.write"
                                ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "newName",
                                    "birthdate": "2000-01-01"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.login").value(responseAccDto.login()))
                .andExpect(jsonPath("$.name").value(responseAccDto.name()))
                .andExpect(jsonPath("$.birthDate").value(responseAccDto.birthDate().toString()))
                .andExpect(jsonPath("$.balance").value(responseAccDto.balance()));
        verify(accountService).editAccount(eq(username), any(EditAccountRequestDto.class));
    }

    @Test
    void shouldTransfer() throws Exception {
        when(accountService.transfer(any(TransferRequestDto.class))).thenReturn(responseDto);
        mockMvc.perform(post(BASE_URL + "/transfer")
                        .with(jwt()
                                .authorities(
                                        () -> "ROLE_SERVICE",
                                        () -> "account.write"
                                ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fromId": 1,
                                    "toId": 2,
                                    "value": 50
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.login").value(responseDto.login()))
                .andExpect(jsonPath("$.name").value(responseDto.name()))
                .andExpect(jsonPath("$.birthDate").value(responseDto.birthDate().toString()))
                .andExpect(jsonPath("$.balance").value(responseDto.balance()));
        verify(accountService).transfer(any(TransferRequestDto.class));
    }

    @Test
    void shouldEditCash() throws Exception {
        when(accountService.editCash(any(EditCashRequestDto.class))).thenReturn(responseDto);
        mockMvc.perform(post(BASE_URL + "/editCash")
                        .with(jwt()
                                .authorities(
                                        () -> "ROLE_SERVICE",
                                        () -> "account.write"
                                ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "accountId": 1,
                                    "value": 50,
                                    "action": "PUT"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.login").value(responseDto.login()))
                .andExpect(jsonPath("$.name").value(responseDto.name()))
                .andExpect(jsonPath("$.birthDate").value(responseDto.birthDate().toString()))
                .andExpect(jsonPath("$.balance").value(responseDto.balance()));
        verify(accountService).editCash(any(EditCashRequestDto.class));
    }
}
