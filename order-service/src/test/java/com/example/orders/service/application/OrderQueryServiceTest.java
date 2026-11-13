package com.example.orders.service.application;

import com.example.orders.contracts.order.OrderSide;
import com.example.orders.contracts.order.OrderStatus;
import com.example.orders.contracts.order.OrderType;
import com.example.orders.contracts.order.SearchOrdersResponse;
import com.example.orders.service.domain.model.Order;
import com.example.orders.service.domain.repository.AccountAccessRepository;
import com.example.orders.service.domain.repository.OrderRepository;
import com.example.orders.service.security.AccessPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

class OrderQueryServiceTest {

    private final AccountAccessRepository access = mock(AccountAccessRepository.class);
    private final OrderRepository orders = mock(OrderRepository.class);
    private final OrderQueryService service = new OrderQueryService(new AccessPolicy(access), orders);
    private final Jwt alice = Jwt.withTokenValue("t").header("alg", "none").subject("alice").build();

    @Test
    void searchesAcrossAllPermittedAccountsWhenNoneRequested() {
        when(access.findAccountIdsBySubject("alice")).thenReturn(Set.of("acc-1", "acc-2"));
        when(orders.findRecentByAccountIds(Set.of("acc-1", "acc-2"), 5)).thenReturn(List.of(order("acc-1")));

        SearchOrdersResponse res = service.searchRecent(alice, 5, null);

        assertThat(res.count()).isEqualTo(1);
        assertThat(res.orders().get(0).accountId()).isEqualTo("acc-1");
    }

    @Test
    void emptyResultIsNotAnError() {
        when(access.findAccountIdsBySubject("alice")).thenReturn(Set.of("acc-1"));
        when(orders.findRecentByAccountIds(Set.of("acc-1"), 5)).thenReturn(List.of());

        SearchOrdersResponse res = service.searchRecent(alice, 5, null);

        assertThat(res.orders()).isEmpty();
        assertThat(res.count()).isZero();
    }

    @Test
    void requestedForeignAccountIsDeniedAndNeverQueried() {
        when(access.findAccountIdsBySubject("alice")).thenReturn(Set.of("acc-1"));

        assertThatThrownBy(() -> service.searchRecent(alice, 5, "acc-of-bob"))
                .isInstanceOf(AccessDeniedException.class);
        verify(orders, never()).findRecentByAccountIds(any(), anyInt());
    }

    private static Order order(String accountId) {
        return new Order(UUID.randomUUID(), accountId, "AAPL", OrderSide.BUY, OrderType.LIMIT,
                new BigDecimal("10"), new BigDecimal("200"), OrderStatus.ACCEPTED, Instant.now());
    }
}
