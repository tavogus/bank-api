package com.bank.api.dto;

import java.math.BigDecimal;

import com.bank.api.entity.PaymentType;

public record CardPurchaseDTO(
    Long cardId,
    BigDecimal amount,
    String description,
    PaymentType paymentType
) {} 