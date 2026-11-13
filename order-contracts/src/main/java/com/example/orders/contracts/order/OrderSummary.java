package com.example.orders.contracts.order;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummary(
        String orderId,
        String accountId,
        String symbol,
        OrderSide side,
        OrderType orderType,
        BigDecimal quantity,
        BigDecimal limitPrice,
        OrderStatus status,
        Instant createdAt) {}
