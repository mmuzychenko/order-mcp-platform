package com.example.orders.service.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

/** Read-flow acceptance tests (spec 11) against real Postgres: security + controller + service + JPA + Flyway. */
@SpringBootTest(properties = "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost/test-issuer")
@AutoConfigureMockMvc
@Testcontainers
class SearchOrdersIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void db(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void seed() {
        jdbc.update("DELETE FROM orders");
        jdbc.update("DELETE FROM account_access");
        jdbc.update("INSERT INTO account_access (subject, account_id) VALUES ('alice', 'acc-1')");
        jdbc.update("INSERT INTO account_access (subject, account_id) VALUES ('bob', 'acc-2')");

        Instant base = Instant.parse("2026-01-01T00:00:00Z");
        for (int i = 0; i < 6; i++) {
            insertOrder("acc-1", base.plus(i, ChronoUnit.HOURS));
        }
        insertOrder("acc-2", base.plus(10, ChronoUnit.HOURS));
    }

    @Test
    void returnsOnlyCallersOrdersNewestFirstUpToLimit() throws Exception {
        mvc.perform(get("/api/v1/orders").param("limit", "5").with(jwt().jwt(j -> j.subject("alice"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5))
                .andExpect(jsonPath("$.orders[*].accountId").value(org.hamcrest.Matchers.everyItem(
                        org.hamcrest.Matchers.is("acc-1"))))
                .andExpect(jsonPath("$.orders[0].createdAt").value("2026-01-01T05:00:00Z"));
    }

    @Test
    void userWithoutAccountsGetsEmptyResult() throws Exception {
        mvc.perform(get("/api/v1/orders").with(jwt().jwt(j -> j.subject("nobody"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.orders").isEmpty());
    }

    @Test
    void foreignAccountIsDenied() throws Exception {
        mvc.perform(get("/api/v1/orders").param("accountId", "acc-2").with(jwt().jwt(j -> j.subject("alice"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    void limitOutOfRangeIsValidationError() throws Exception {
        mvc.perform(get("/api/v1/orders").param("limit", "0").with(jwt().jwt(j -> j.subject("alice"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("limit"));
    }

    @Test
    void nonNumericLimitIsValidationError() throws Exception {
        mvc.perform(get("/api/v1/orders").param("limit", "abc").with(jwt().jwt(j -> j.subject("alice"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void missingTokenIsUnauthorized() throws Exception {
        mvc.perform(get("/api/v1/orders")).andExpect(status().isUnauthorized());
    }

    private void insertOrder(String accountId, Instant createdAt) {
        jdbc.update("""
                INSERT INTO orders (id, account_id, symbol, side, order_type, quantity, limit_price, status, created_at)
                VALUES (?, ?, 'AAPL', 'BUY', 'LIMIT', 10, 200, 'ACCEPTED', ?)
                """, UUID.randomUUID(), accountId, Timestamp.from(createdAt));
    }
}
