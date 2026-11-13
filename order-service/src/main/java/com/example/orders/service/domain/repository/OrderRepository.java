package com.example.orders.service.domain.repository;

import com.example.orders.service.domain.model.Order;

import java.util.Collection;
import java.util.List;

public interface OrderRepository {

    /** Most recent orders first, restricted to the given accounts. Callers must pass an already-authorized scope. */
    List<Order> findRecentByAccountIds(Collection<String> accountIds, int limit);
}
