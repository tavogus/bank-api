package com.bank.api.dto;

public record UserDTO(
    Long id,
    String email,
    String fullName,
    String cpf
) {}