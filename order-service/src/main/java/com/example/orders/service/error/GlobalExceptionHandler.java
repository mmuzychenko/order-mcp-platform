package com.example.orders.service.error;

import com.example.orders.contracts.error.ErrorCode;
import com.example.orders.contracts.error.ErrorEnvelope;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/** Maps exceptions to the stable envelope. No stack traces leave the service. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorEnvelope> accessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, ErrorCode.ACCESS_DENIED, "Access denied", false);
    }

    /** Query-parameter constraints (@Min/@Max on controller arguments). */
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorEnvelope> constraintViolation(ConstraintViolationException ex) {
        List<ErrorEnvelope.FieldError> fields = ex.getConstraintViolations().stream()
                .map(v -> new ErrorEnvelope.FieldError(leafName(v.getPropertyPath().toString()), v.getMessage()))
                .toList();
        return validationError(fields);
    }

    /** Request-body constraints (@Valid). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorEnvelope> bodyNotValid(MethodArgumentNotValidException ex) {
        List<ErrorEnvelope.FieldError> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new ErrorEnvelope.FieldError(f.getField(), f.getDefaultMessage()))
                .toList();
        return validationError(fields);
    }

    /** e.g. limit=abc. The rejected value is deliberately not echoed back. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorEnvelope> typeMismatch(MethodArgumentTypeMismatchException ex) {
        return validationError(List.of(new ErrorEnvelope.FieldError(ex.getName(), "Invalid value")));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorEnvelope> unexpected(Exception ex) {
        log.error("Unhandled error", ex); // server-side only
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, "Internal error", true);
    }

    // TODO(Milestone 2): NOT_FOUND, BUSINESS_RULE_VIOLATION, CONFLICT

    private ResponseEntity<ErrorEnvelope> validationError(List<ErrorEnvelope.FieldError> fields) {
        return ResponseEntity.badRequest()
                .body(new ErrorEnvelope(ErrorCode.VALIDATION_ERROR, "Validation failed",
                        MDC.get("correlationId"), fields, false));
    }

    /** "search.limit" -> "limit" */
    private static String leafName(String path) {
        return path.substring(path.lastIndexOf('.') + 1);
    }

    private ResponseEntity<ErrorEnvelope> build(HttpStatus status, ErrorCode code, String msg, boolean retryable) {
        return ResponseEntity.status(status)
                .body(new ErrorEnvelope(code, msg, MDC.get("correlationId"), List.of(), retryable));
    }
}
