package com.bank.accountapp.repository;

import com.bank.accountapp.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ImportAutoConfiguration(exclude = LiquibaseAutoConfiguration.class)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("save — должен сохранить account")
    void shouldSaveAccount() {
        Account account = newAccount(
                "user1",
                "test user1",
                LocalDate.of(1990, 1, 1),
                new BigDecimal(1000));
        Account saved = accountRepository.save(account);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo(account.getUsername());
        assertThat(saved.getName()).isEqualTo(account.getName());
        assertThat(saved.getBirthDate()).isEqualTo(account.getBirthDate());
        assertThat(saved.getBalance()).isEqualTo(account.getBalance());
    }

    @Test
    @DisplayName("findByUsername — должен вернуть account, если найден")
    void shouldFindUserByUsername() {
        Account account = newAccount(
                "user2",
                "test user2",
                LocalDate.of(1991, 1, 1),
                new BigDecimal(1000));
        Account saved = accountRepository.save(account);

        Optional<Account> result = accountRepository.findByUsername(saved.getUsername());

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(account.getUsername());
        assertThat(result.get().getName()).isEqualTo(account.getName());
        assertThat(result.get().getBirthDate()).isEqualTo(account.getBirthDate());
        assertThat(result.get().getBalance()).isEqualTo(account.getBalance());
    }

    @Test
    @DisplayName("findByUsername(empty_user) — должен вернуть пусто, на несуществующего account")
    void shouldReturnEmptyIfAccountNotFound() {
        Optional<Account> result = accountRepository.findByUsername("empty_user");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllByUsernameNot(String username) — должен вернуть все accounts, креме username")
    void shouldFindUserById() {
        Account account3 = newAccount(
                "user3",
                "test user3",
                LocalDate.of(1991, 1, 1),
                new BigDecimal(1000));
        Account account4 = newAccount(
                "user4",
                "test user4",
                LocalDate.of(1991, 1, 1),
                new BigDecimal(1000));
        Account account5 = newAccount(
                "user5",
                "test user5",
                LocalDate.of(1991, 1, 1),
                new BigDecimal(1000));
        List<Account> saved = accountRepository.saveAll(List.of(account3, account4, account5));
        List<Account> result = accountRepository.findAllByUsernameNot(account3.getUsername());
        assertThat(result)
                .hasSize(2)
                .extracting(Account::getUsername)
                .containsExactlyInAnyOrder(account4.getUsername(), account5.getUsername());

        assertThat(result)
                .extracting(Account::getUsername)
                .doesNotContain(account3.getUsername());
    }

    private Account newAccount(String username, String name, LocalDate birthDate, BigDecimal balance) {
        return Account.builder()
                .username(username)
                .name(name)
                .birthDate(birthDate)
                .balance(balance)
                .build();
    }
}
