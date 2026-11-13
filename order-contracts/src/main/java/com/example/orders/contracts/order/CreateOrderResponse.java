package com.example.orders.contracts.order;

import java.time.Instant;

public record CreateOrderResponse(String orderId, OrderStatus status, String operationId, Instant createdAt) {}
