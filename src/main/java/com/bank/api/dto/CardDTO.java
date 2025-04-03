package com.bank.api.dto;

import java.time.LocalDateTime;

public record CardDTO(
    Long id,
    String cardNumber,
    String cardHolderName,
    LocalDateTime expirationDate,
    Long userId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}