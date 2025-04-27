package com.bank.api.dto.security;

public record AccountCredentialsDTO(
        String username,
        String password
) {}