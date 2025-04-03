package com.bank.api.dto;

import java.math.BigDecimal;

public record AccountOperationDTO(
    BigDecimal amount
) {} 