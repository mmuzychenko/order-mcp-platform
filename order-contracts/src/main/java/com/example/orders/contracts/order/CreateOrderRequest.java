package com.example.orders.contracts.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/** Milestone 2. limitPrice required when orderType == LIMIT (validated in the domain, not here). */
public record CreateOrderRequest(
        @NotBlank String accountId,
        @NotBlank String symbol,
        @NotNull OrderSide side,
        @NotNull @Positive BigDecimal quantity,
        @NotNull OrderType orderType,
        BigDecimal limitPrice,
        @NotBlank String idempotencyKey) {}
