package com.bank.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bank.api.entity.PaymentType;
import com.bank.api.entity.TransactionStatus;
import com.bank.api.entity.TransactionType;

public record TransactionResponseDTO(
    Long id,
    String sourceAccountNumber,
    String destinationAccountNumber,
    BigDecimal amount,
    TransactionStatus status,
    TransactionType type,
    PaymentType paymentType,
    String description,
    LocalDateTime createdAt
) {} 