package com.example.orders.service.security;

import com.example.orders.service.domain.repository.AccountAccessRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** Acceptance matrix (spec 11): unauthorized account/tenant -> denied, no data exposure. */
class AccessPolicyTest {

    private final AccountAccessRepository repo = mock(AccountAccessRepository.class);
    private final AccessPolicy policy = new AccessPolicy(repo);

    @Test
    void allowsOwnAccount() {
        when(repo.findAccountIdsBySubject("alice")).thenReturn(Set.of("acc-1"));

        policy.requireAccountAccess(jwt("alice"), "acc-1");
    }

    @Test
    void deniesCrossTenantAccess() {
        when(repo.findAccountIdsBySubject("alice")).thenReturn(Set.of("acc-1"));

        assertThatThrownBy(() -> policy.requireAccountAccess(jwt("alice"), "acc-of-bob"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void unknownSubjectHasNoAccounts() {
        when(repo.findAccountIdsBySubject("nobody")).thenReturn(Set.of());

        assertThat(policy.permittedAccounts(jwt("nobody"))).isEmpty();
    }

    @Test
    void blankSubjectIsDeniedWithoutLookup() {
        assertThat(policy.permittedAccounts(jwt(" "))).isEmpty();
        verifyNoInteractions(repo);
    }

    private static Jwt jwt(String subject) {
        return Jwt.withTokenValue("t").header("alg", "none").subject(subject).build();
    }
}
