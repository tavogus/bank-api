package com.bank.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.bank.api.entity.InvoiceStatus;

public record InvoiceDTO(
    Long id,
    Long cardId,
    LocalDate dueDate,
    LocalDate closingDate,
    BigDecimal totalAmount,
    InvoiceStatus status,
    List<TransactionResponseDTO> transactions
) {} 