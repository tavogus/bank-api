package com.bank.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountDTO(
    Long id,
    String accountNumber,
    BigDecimal balance,
    Long userId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}