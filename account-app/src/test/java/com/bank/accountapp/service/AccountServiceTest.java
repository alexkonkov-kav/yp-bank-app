package com.bank.accountapp.service;

import com.bank.accountapp.client.NotificationClient;
import com.bank.accountapp.dto.account.AccountResponseDto;
import com.bank.accountapp.dto.account.EditAccountRequestDto;
import com.bank.accountapp.dto.cash.CashAction;
import com.bank.accountapp.dto.cash.EditCashRequestDto;
import com.bank.accountapp.dto.transfer.TransferRequestDto;
import com.bank.accountapp.mapper.AccountMapper;
import com.bank.accountapp.model.Account;
import com.bank.accountapp.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private AccountMapper mapper;

    @Test
    void shouldReturnAccountDtoWhenAccountExists() {
        String username = "user1";
        Account account = new Account();
        account.setUsername(username);
        AccountResponseDto dto = new AccountResponseDto("user1", "test user1",
                LocalDate.of(1990, 1, 1), new BigDecimal(1000));
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));
        when(mapper.toAccountResponseDto(account)).thenReturn(dto);
        AccountResponseDto result = accountService.getAccountByUsername(username);

        assertThat(result).isNotNull();
        assertThat(result.login()).isEqualTo(username);
        verify(accountRepository).findByUsername(username);
        verify(mapper).toAccountResponseDto(account);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        String username = "not_found";
        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accountService.getAccountByUsername(username))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Аккаунт не найден");

        verify(accountRepository).findByUsername(username);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldReturnAccountsForTransfer() {
        String username = "user1";
        Account account2 = new Account();
        account2.setUsername("user2");
        Account account3 = new Account();
        account3.setUsername("user3");
        List<Account> accounts = List.of(account2, account3);
        AccountResponseDto dto2 = new AccountResponseDto("user2", "test user2",
                LocalDate.of(1990, 1, 1), new BigDecimal(1000));
        AccountResponseDto dto3 = new AccountResponseDto("user3", "test user3",
                LocalDate.of(1990, 1, 1), new BigDecimal(1000));

        when(accountRepository.findAllByUsernameNot(username)).thenReturn(accounts);
        when(mapper.toAccountResponseDto(account2)).thenReturn(dto2);
        when(mapper.toAccountResponseDto(account3)).thenReturn(dto3);
        List<AccountResponseDto> result = accountService.getAccountsForTransfer(username);
        assertThat(result)
                .hasSize(2)
                .extracting(AccountResponseDto::login)
                .containsExactlyInAnyOrder("user2", "user3");

        verify(accountRepository).findAllByUsernameNot(username);
        verify(mapper).toAccountResponseDto(account2);
        verify(mapper).toAccountResponseDto(account3);
    }

    @Test
    void shouldEditAccountAndSendNotification() {
        String username = "user1";
        Account account = new Account();
        account.setUsername(username);
        account.setName("oldName");
        account.setBirthDate(LocalDate.of(1990, 1, 1));
        EditAccountRequestDto request = new EditAccountRequestDto("newName", LocalDate.of(2000, 1, 1));
        Account savedAccount = new Account();
        savedAccount.setUsername(username);
        savedAccount.setName("newName");
        savedAccount.setBirthDate(LocalDate.of(2000, 1, 1));

        AccountResponseDto responseDto = new AccountResponseDto(username, savedAccount.getName(),
                LocalDate.of(1990, 1, 1), new BigDecimal(1000));
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(mapper.toAccountResponseDto(savedAccount)).thenReturn(responseDto);
        AccountResponseDto result = accountService.editAccount(username, request);

        assertThat(account.getName()).isEqualTo("newName");
        assertThat(account.getBirthDate()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(result).isNotNull();
        assertThat(result.login()).isEqualTo(username);

        verify(accountRepository).findByUsername(username);
        verify(accountRepository).save(account);
        verify(notificationClient).sendEditAccountNotification(any());
        verify(mapper).toAccountResponseDto(savedAccount);
    }

    @Test
    void shouldIncreaseBalanceOnPut() {
        Long accountId = 1L;
        Account account = new Account();
        account.setId(accountId);
        account.setBalance(BigDecimal.valueOf(100));
        EditCashRequestDto request = new EditCashRequestDto(accountId, 50, CashAction.PUT);
        Account saved = new Account(accountId, "user1", "test user1",
                LocalDate.of(1990, 1, 1), BigDecimal.valueOf(150));

        AccountResponseDto dto = new AccountResponseDto(saved.getUsername(), saved.getName(),
                saved.getBirthDate(), saved.getBalance());
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(saved);
        when(mapper.toAccountResponseDto(saved)).thenReturn(dto);
        AccountResponseDto result = accountService.editCash(request);

        assertThat(result.balance()).isEqualByComparingTo("150");
        verify(accountRepository).save(account);
        verify(mapper).toAccountResponseDto(saved);
    }

    @Test
    void shouldTransferMoneyBetweenAccounts() {
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = BigDecimal.valueOf(50);
        Account fromAccount = new Account(fromId, "user1", "test user1",
                LocalDate.of(1990, 1, 1), BigDecimal.valueOf(100));

        Account toAccount = new Account(toId, "user2", "test user2",
                LocalDate.of(1990, 1, 1), BigDecimal.valueOf(200));
        TransferRequestDto request = new TransferRequestDto(fromId, toId, amount);
        Account savedFrom = new Account();
        savedFrom.setId(fromId);
        savedFrom.setBalance(BigDecimal.valueOf(50));
        Account savedTo = new Account();
        savedTo.setId(toId);
        savedTo.setBalance(BigDecimal.valueOf(250));

        AccountResponseDto responseDto = new AccountResponseDto(savedFrom.getUsername(), savedFrom.getName(),
                savedFrom.getBirthDate(), savedFrom.getBalance());
        when(accountRepository.findById(fromId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toId)).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(fromAccount)).thenReturn(savedFrom);
        when(accountRepository.save(toAccount)).thenReturn(savedTo);
        when(mapper.toAccountResponseDto(savedFrom)).thenReturn(responseDto);
        AccountResponseDto result = accountService.transfer(request);

        assertThat(fromAccount.getBalance()).isEqualByComparingTo("50");
        assertThat(toAccount.getBalance()).isEqualByComparingTo("250");
        verify(accountRepository).save(fromAccount);
        verify(accountRepository).save(toAccount);
        verify(notificationClient).sendTransferNotification(any());
        verify(mapper).toAccountResponseDto(savedFrom);
    }
}
