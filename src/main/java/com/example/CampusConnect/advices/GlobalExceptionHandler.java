package com.example.CampusConnect.advices;

import com.example.CampusConnect.exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /* ==========================
       BUSINESS EXCEPTIONS
       ========================== */

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateResourceException ex) {

        log.warn("Duplicate resource detected: {}", ex.getMessage());

        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {

        log.warn("Resource not found: {}", ex.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /* ==========================
       VALIDATION
       ========================== */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {

        log.warn("Validation failed");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", 400);
        response.put("errors", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    /* ==========================
       DATABASE
       ========================== */

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {

        log.error("Database constraint violation", ex);

        return buildResponse(HttpStatus.BAD_REQUEST,
                "Invalid data operation");
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {

        log.warn("Optimistic locking conflict");

        return buildResponse(HttpStatus.CONFLICT,
                "This record was modified by another user. Please retry.");
    }

    /* ==========================
       SPRING SECURITY AUTHENTICATION
       ========================== */

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {

        return buildResponse(HttpStatus.UNAUTHORIZED,
                "Invalid email or password");
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleLockedAccount(LockedException ex) {

        return buildResponse(HttpStatus.FORBIDDEN,
                "User account is blocked");
    }

    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleAccountExpired(AccountExpiredException ex) {

        return buildResponse(HttpStatus.UNAUTHORIZED,
                "User account has expired");
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleCredentialsExpired(CredentialsExpiredException ex) {

        return buildResponse(HttpStatus.UNAUTHORIZED,
                "User credentials have expired");
    }

    // 🔥 SINGLE GENERIC FALLBACK FOR AUTHENTICATION
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {

        log.warn("Authentication failure: {}", ex.getMessage());

        return buildResponse(HttpStatus.UNAUTHORIZED,
                "Authentication failed");
    }

    /* ==========================
       JWT EXCEPTIONS
       ========================== */

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwt(ExpiredJwtException ex) {

        return buildResponse(HttpStatus.UNAUTHORIZED,
                "JWT token has expired");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJwt(MalformedJwtException ex) {

        return buildResponse(HttpStatus.UNAUTHORIZED,
                "Malformed JWT token");
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSignature(SignatureException ex) {

        return buildResponse(HttpStatus.UNAUTHORIZED,
                "Invalid JWT signature");
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedJwt(UnsupportedJwtException ex) {

        return buildResponse(HttpStatus.UNAUTHORIZED,
                "Unsupported JWT token");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {

        return buildResponse(HttpStatus.UNAUTHORIZED,
                "JWT token is invalid or empty");
    }

    /* ==========================
       GLOBAL FALLBACK
       ========================== */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {

        log.error("Unhandled exception occurred", ex);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error");
    }

    /* ==========================
       COMMON RESPONSE BUILDER
       ========================== */

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {

        return ResponseEntity.status(status)
                .body(Map.of(
                        "timestamp", Instant.now(),
                        "status", status.value(),
                        "error", message
                ));
    }
}
