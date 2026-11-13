package com.example.orders.contracts.error;

/** Stable business error categories (spec section 9). */
public enum ErrorCode {
    VALIDATION_ERROR,
    NOT_FOUND,
    ACCESS_DENIED,
    BUSINESS_RULE_VIOLATION,
    CONFLICT,
    EXTERNAL_SERVICE_ERROR,
    INTERNAL_ERROR
}
