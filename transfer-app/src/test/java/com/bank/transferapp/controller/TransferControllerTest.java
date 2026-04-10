package com.bank.transferapp.controller;

import com.bank.transferapp.config.SecurityConfig;
import com.bank.transferapp.dto.AccountResponseDto;
import com.bank.transferapp.dto.TransferRequestDto;
import com.bank.transferapp.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
@Import(SecurityConfig.class)
public class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    @Test
    void shouldTransfer_whenUserHasRoleAndAuthority() throws Exception {
        String username = "user1";
        AccountResponseDto response = new AccountResponseDto(
                username,
                "Test User",
                LocalDate.of(1990, 1, 1),
                new BigDecimal(1000)
        );
        when(transferService.transfer(eq(username), any(TransferRequestDto.class))).thenReturn(response);
        mockMvc.perform(post("/transfer")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("preferred_username", username))
                                .authorities(
                                        () -> "ROLE_USER",
                                        () -> "transfer.write"
                                ))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                
                                                        {
                                  "login": "user2",
                                  "value": 100
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.login").value(username))
                .andExpect(jsonPath("$.balance").value(1000));
        verify(transferService).transfer(eq(username), any(TransferRequestDto.class));
    }

    @Test
    void shouldReturnForbidden_whenNoAuthority() throws Exception {
        mockMvc.perform(post("/transfer")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("preferred_username", "user1"))
                                .authorities(() -> "ROLE_USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "user2",
                                  "value": 100
                                }
                                """))
                .andExpect(status().isForbidden());
        verifyNoInteractions(transferService);
    }

    @Test
    void shouldReturnUnauthorized_whenNoJwt() throws Exception {
        mockMvc.perform(post("/transfer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "user2",
                                  "value": 100
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}
