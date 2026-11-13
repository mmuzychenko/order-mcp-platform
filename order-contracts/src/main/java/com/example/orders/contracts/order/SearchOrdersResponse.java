package com.example.orders.contracts.order;

import java.util.List;

public record SearchOrdersResponse(List<OrderSummary> orders, int count, String nextCursor) {}
