package com.example.orders.service.security;

import com.example.orders.service.domain.repository.AccountAccessRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Set;

/** Java independently enforces account/tenant scope (spec section 7). Deny by default. */
@Component
public class AccessPolicy {

    private final AccountAccessRepository accounts;

    public AccessPolicy(AccountAccessRepository accounts) {
        this.accounts = accounts;
    }

    /** Accounts the caller may access, derived from the token subject only. Empty means no access. */
    public Set<String> permittedAccounts(Jwt principal) {
        String subject = principal.getSubject();
        if (subject == null || subject.isBlank()) {
            return Set.of();
        }
        return accounts.findAccountIdsBySubject(subject);
    }

    public void requireAccountAccess(Jwt principal, String accountId) {
        if (!permittedAccounts(principal).contains(accountId)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
