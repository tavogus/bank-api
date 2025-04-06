package com.bank.api.service;

import com.bank.api.dto.AccountDTO;
import com.bank.api.entity.Account;
import com.bank.api.entity.User;
import com.bank.api.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountDTO createAccount() {
        User user = UserContextService.getCurrentUser();

        if (user == null) {
            return null;
        }
        
        if (accountRepository.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("User already has an account");
        }

        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountNumber(generateAccountNumber());
        
        account = accountRepository.save(account);
        return new AccountDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getUser().getId(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    @Transactional
    public AccountDTO deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be positive");
        }

        Account account = getAccount();

        account.setBalance(account.getBalance().add(amount));
        account = accountRepository.save(account);
        return new AccountDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getUser().getId(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    @Transactional
    public AccountDTO withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be positive");
        }

        Account account = getAccount();

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        account = accountRepository.save(account);
        return new AccountDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getUser().getId(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    public AccountDTO getAccountByUserId() {
        Account account = getAccount();
        return new AccountDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getUser().getId(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    private static Account getAccount() {
        User user = UserContextService.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user.getAccount();
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = UUID.randomUUID().toString().substring(0, 8);
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
} 