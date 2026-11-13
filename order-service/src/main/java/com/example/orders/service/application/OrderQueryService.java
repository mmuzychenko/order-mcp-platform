package com.example.orders.service.application;

import com.example.orders.contracts.order.OrderSummary;
import com.example.orders.contracts.order.SearchOrdersResponse;
import com.example.orders.service.domain.model.Order;
import com.example.orders.service.domain.repository.OrderRepository;
import com.example.orders.service.security.AccessPolicy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class OrderQueryService {

    private final AccessPolicy accessPolicy;
    private final OrderRepository orders;

    public OrderQueryService(AccessPolicy accessPolicy, OrderRepository orders) {
        this.accessPolicy = accessPolicy;
        this.orders = orders;
    }

    @Transactional(readOnly = true)
    public SearchOrdersResponse searchRecent(Jwt principal, int limit, String requestedAccountId) {
        Set<String> scope = accessPolicy.permittedAccounts(principal);
        if (requestedAccountId != null) {
            if (!scope.contains(requestedAccountId)) {
                throw new AccessDeniedException("Access denied");
            }
            scope = Set.of(requestedAccountId);
        }

        List<OrderSummary> summaries = orders.findRecentByAccountIds(scope, limit).stream()
                .map(OrderQueryService::toSummary)
                .toList();
        return new SearchOrdersResponse(summaries, summaries.size(), null);
    }

    private static OrderSummary toSummary(Order o) {
        return new OrderSummary(o.getId().toString(), o.getAccountId(), o.getSymbol(), o.getSide(),
                o.getOrderType(), o.getQuantity(), o.getLimitPrice(), o.getStatus(), o.getCreatedAt());
    }
}
