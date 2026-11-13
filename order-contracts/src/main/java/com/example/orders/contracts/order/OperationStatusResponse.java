package com.example.orders.contracts.order;

/** Resolves unknown write outcomes after timeouts (spec 5.8 and 8). */
public record OperationStatusResponse(String operationId, String state, String orderId) {}
