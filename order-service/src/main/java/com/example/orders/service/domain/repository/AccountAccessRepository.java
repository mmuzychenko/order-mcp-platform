package com.example.orders.service.domain.repository;

import java.util.Set;

public interface AccountAccessRepository {

    Set<String> findAccountIdsBySubject(String subject);
}
