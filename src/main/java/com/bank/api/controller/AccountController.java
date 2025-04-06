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

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount() {
        return ResponseEntity.ok(accountService.createAccount());
    }

    @PostMapping("/deposit")
    public ResponseEntity<AccountDTO> deposit(
            @RequestBody AccountOperationDTO operation) {
        return ResponseEntity.ok(accountService.deposit(operation.amount()));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AccountDTO> withdraw(
            @RequestBody AccountOperationDTO operation) {
        return ResponseEntity.ok(accountService.withdraw(operation.amount()));
    }

    @GetMapping("/user")
    public ResponseEntity<AccountDTO> getAccountByUserId() {
        return ResponseEntity.ok(accountService.getAccountByUserId());
    }
} 