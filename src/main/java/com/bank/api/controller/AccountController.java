package com.bank.api.controller;

import com.bank.api.dto.AccountDTO;
import com.bank.api.dto.AccountOperationDTO;
import com.bank.api.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<AccountDTO> createAccount(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.createAccount(userId));
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<AccountDTO> deposit(
            @PathVariable Long accountId,
            @RequestBody AccountOperationDTO operation) {
        return ResponseEntity.ok(accountService.deposit(accountId, operation.amount()));
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<AccountDTO> withdraw(
            @PathVariable Long accountId,
            @RequestBody AccountOperationDTO operation) {
        return ResponseEntity.ok(accountService.withdraw(accountId, operation.amount()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<AccountDTO> getAccountByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountByUserId(userId));
    }
} 