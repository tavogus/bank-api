package com.bank.api.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bank.api.entity.Account;

@Service
public class UserContextService {
    
    private final AccountService accountService;
    
    public UserContextService(AccountService accountService) {
        this.accountService = accountService;
    }
    
    public Account getCurrentUserAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountService.findByUsername(username);
    }
} 