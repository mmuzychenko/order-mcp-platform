package com.example.orders.service.infrastructure.persistence;

import com.example.orders.service.domain.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

interface SpringDataOrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByAccountIdInOrderByCreatedAtDesc(Collection<String> accountIds, Pageable pageable);
}
