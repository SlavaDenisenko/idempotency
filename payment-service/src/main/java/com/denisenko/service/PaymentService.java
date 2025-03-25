package com.denisenko.service;

import com.denisenko.dto.AccountDto;
import com.denisenko.model.Account;
import com.denisenko.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final AccountRepository accountRepository;

    public AccountDto createAccount(AccountDto accountDto) {
        Account account = mapToEntity(accountDto);
        account.setReserved(BigDecimal.ZERO);
        return mapToDto(accountRepository.save(account));
    }

    public AccountDto getAccount(Long userId) {
        Account account = accountRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Account not found for user: " + userId));
        return mapToDto(account);
    }

    public boolean processPayment(Long userId, BigDecimal amount) {
        Account account = accountRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Account not found for user: " + userId));
        if (account.getBalance().compareTo(amount) < 0) return false;
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        return true;
    }

    private Account mapToEntity(AccountDto accountDto) {
        return Account.builder()
                .userId(accountDto.getUserId())
                .balance(accountDto.getBalance())
                .build();
    }

    private AccountDto mapToDto(Account account) {
        return AccountDto.builder()
                .userId(account.getUserId())
                .balance(account.getBalance())
                .reserved(account.getReserved())
                .build();
    }
}
