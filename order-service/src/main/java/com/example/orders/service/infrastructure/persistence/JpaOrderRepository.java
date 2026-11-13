package com.example.orders.service.infrastructure.persistence;

import com.example.orders.service.domain.model.Order;
import com.example.orders.service.domain.repository.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
class JpaOrderRepository implements OrderRepository {

    private final SpringDataOrderRepository delegate;

    JpaOrderRepository(SpringDataOrderRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Order> findRecentByAccountIds(Collection<String> accountIds, int limit) {
        if (accountIds.isEmpty()) {
            return List.of(); // empty IN () is invalid SQL on some dialects, and means "no access" anyway
        }
        return delegate.findByAccountIdInOrderByCreatedAtDesc(accountIds, PageRequest.of(0, limit));
    }
}
