package com.bank.accountapp.service;

import com.bank.accountapp.client.NotificationClient;
import com.bank.accountapp.dto.account.AccountIdResponseDto;
import com.bank.accountapp.dto.account.AccountResponseDto;
import com.bank.accountapp.dto.account.EditAccountRequestDto;
import com.bank.accountapp.dto.cash.EditCashRequestDto;
import com.bank.accountapp.dto.notification.EditAccountNotificationRequestDto;
import com.bank.accountapp.dto.notification.TransferNotificationRequestDto;
import com.bank.accountapp.dto.transfer.TransferRequestDto;
import com.bank.accountapp.mapper.AccountMapper;
import com.bank.accountapp.model.Account;
import com.bank.accountapp.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final NotificationClient notificationClient;
    private final AccountMapper mapper;

    public AccountService(AccountRepository accountRepository,
                          NotificationClient notificationClient,
                          AccountMapper mapper) {
        this.accountRepository = accountRepository;
        this.notificationClient = notificationClient;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Account getAccount(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Аккаунт не найден"));
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Аккаунт не найден"));
    }

    @Transactional(readOnly = true)
    public AccountResponseDto getAccountByUsername(String username) {
        return mapper.toAccountResponseDto(getAccount(username));
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAccountsForTransfer(String username) {
        return accountRepository.findAllByUsernameNot(username)
                .stream()
                .map(mapper::toAccountResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountIdResponseDto getIdAccountByUsername(String username) {
        return new AccountIdResponseDto(getAccount(username).getId());
    }

    @Transactional
    public AccountResponseDto editAccount(String username, EditAccountRequestDto requestDto) {
        Account account = getAccount(username);
        String oldName = account.getName();
        LocalDate oldBirthDate = account.getBirthDate();
        if (!requestDto.name().isBlank()) {
            account.setName(requestDto.name());
        }
        if (requestDto.birthdate() != null) {
            account.setBirthDate(requestDto.birthdate());
        }
        Account savedAccount = accountRepository.save(account);
        notificationClient.sendEditAccountNotification(getEditAccountNotificationRequestDto(username, oldName, oldBirthDate, savedAccount));

        return mapper.toAccountResponseDto(savedAccount);
    }

    @Transactional
    public AccountResponseDto editCash(EditCashRequestDto requestDto) {
        BigDecimal amount = BigDecimal.valueOf(requestDto.value());
        checkAmount(amount);
        Account account = getAccountById(requestDto.accountId());
        switch (requestDto.action()) {
            case PUT -> account.setBalance(account.getBalance().add(amount));
            case GET -> {
                checkBalance(account, amount);
                account.setBalance(account.getBalance().subtract(amount));
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неизвестный тип операции");
        }
        Account savedAccount = accountRepository.save(account);

        return mapper.toAccountResponseDto(savedAccount);
    }

    @Transactional
    public AccountResponseDto transfer(TransferRequestDto requestDto) {
        BigDecimal amount = requestDto.value();
        checkAmount(amount);

        Account fromAccount = getAccountById(requestDto.fromId());
        Account toAccount = getAccountById(requestDto.toId());
        checkBalance(fromAccount, amount);

        BigDecimal fromAccountOldBalance = fromAccount.getBalance();
        BigDecimal toAccountOldBalance = toAccount.getBalance();

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        Account savedFromAccount = accountRepository.save(fromAccount);
        Account savedToAccount = accountRepository.save(toAccount);
        notificationClient.sendTransferNotification(getTransferNotificationRequestDto(
                fromAccountOldBalance, toAccountOldBalance,
                savedFromAccount, savedToAccount, requestDto));

        return mapper.toAccountResponseDto(savedFromAccount);
    }

    private void checkAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Сумма должна быть больше 0");
        }
    }

    private void checkBalance(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недостаточно средств на счёте");
        }
    }

    private EditAccountNotificationRequestDto getEditAccountNotificationRequestDto(String username, String oldName, LocalDate oldBirthDate, Account account) {
        return new EditAccountNotificationRequestDto(username,
                oldName, oldBirthDate,
                account.getName(), account.getBirthDate());
    }

    private TransferNotificationRequestDto getTransferNotificationRequestDto(BigDecimal fromAccountOldBalance, BigDecimal toAccountOldBalance,
                                                                             Account savedFromAccount, Account savedToAccount,
                                                                             TransferRequestDto requestDto) {
        return new TransferNotificationRequestDto(savedFromAccount.getUsername(), savedFromAccount.getName(),
                fromAccountOldBalance, savedFromAccount.getBalance(),
                savedToAccount.getUsername(), savedToAccount.getName(),
                toAccountOldBalance, savedToAccount.getBalance(),
                requestDto.value());
    }
}
