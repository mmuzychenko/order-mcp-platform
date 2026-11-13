package com.example.orders.service.infrastructure.persistence;

import com.example.orders.service.domain.repository.AccountAccessRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
class JdbcAccountAccessRepository implements AccountAccessRepository {

    private final JdbcTemplate jdbc;

    JdbcAccountAccessRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Set<String> findAccountIdsBySubject(String subject) {
        return new HashSet<>(jdbc.queryForList(
                "SELECT account_id FROM account_access WHERE subject = ?", String.class, subject));
    }
}
