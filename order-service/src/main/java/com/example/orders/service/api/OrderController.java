package com.example.orders.service.api;

import com.example.orders.contracts.order.SearchOrdersResponse;
import com.example.orders.service.application.OrderQueryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderQueryService queryService;

    public OrderController(OrderQueryService queryService) {
        this.queryService = queryService;
    }

    /** Milestone 1: read flow. Scope is derived from the authenticated principal only. */
    @GetMapping
    public SearchOrdersResponse search(
            @AuthenticationPrincipal Jwt principal,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String accountId) {
        return queryService.searchRecent(principal, limit, accountId);
    }

    // TODO(Milestone 2): POST /api/v1/orders (Idempotency-Key header) -> OrderCommandService
    // TODO(Milestone 2): GET  /api/v1/orders/{orderId}
    // TODO(Milestone 2): GET  /api/v1/operations/{operationId}
}
