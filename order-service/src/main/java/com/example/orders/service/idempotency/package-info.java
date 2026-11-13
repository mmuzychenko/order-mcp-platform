/**
 * Milestone 2 (D6): persistent idempotency records.
 * Key scope = (principal, key); store a payload hash; same key + different payload -> CONFLICT;
 * concurrent duplicates resolved by a unique constraint; duplicates return the prior outcome.
 */
package com.example.orders.service.idempotency;
