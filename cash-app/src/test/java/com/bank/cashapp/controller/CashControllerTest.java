package com.bank.cashapp.controller;

import com.bank.cashapp.config.SecurityConfig;
import com.bank.cashapp.dto.AccountResponseDto;
import com.bank.cashapp.dto.EditCashRequestDto;
import com.bank.cashapp.service.CashService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CashController.class)
@Import(SecurityConfig.class)
public class CashControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CashService cashService;

    @Test
    void shouldEditCash_whenUserHasRoleAndAuthority() throws Exception {
        String username = "user1";
        AccountResponseDto response = new AccountResponseDto(
                username,
                "test user1",
                LocalDate.of(1990, 1, 1),
                new BigDecimal(150)
        );
        when(cashService.editCash(eq(username), any(EditCashRequestDto.class))).thenReturn(response);
        mockMvc.perform(post("/cash")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("preferred_username", username))
                                .authorities(
                                        () -> "ROLE_USER",
                                        () -> "cash.write"
                                ))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                
                                    {
                                    "value": 50,
                                    "action": "PUT"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.login").value(username))
                .andExpect(jsonPath("$.balance").value(150));
        verify(cashService).editCash(eq(username), any(EditCashRequestDto.class));
    }

    @Test
    void shouldReturnForbidden_whenNoAuthority() throws Exception {
        mockMvc.perform(post("/cash")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("preferred_username", "user1"))
                                .authorities(() -> "ROLE_USER")) // нет cash.write
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "value": 50,
                                    "action": "PUT"
                                }
                                """))
                .andExpect(status().isForbidden());
        verifyNoInteractions(cashService);
    }

}
