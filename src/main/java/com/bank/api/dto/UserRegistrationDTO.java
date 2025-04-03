package com.bank.api.dto;

public record UserRegistrationDTO(
    String email,
    String password,
    String fullName,
    String cpf
) {} 