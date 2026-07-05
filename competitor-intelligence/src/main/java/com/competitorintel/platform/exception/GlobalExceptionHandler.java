package com.competitorintel.platform.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralised exception handler producing RFC-7807 ProblemDetail responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex, WebRequest req) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ProblemDetail> handleDuplicate(DuplicateResourceException ex, WebRequest req) {
        return problem(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(BadRequestException ex, WebRequest req) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex,
                                                           WebRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation Failed");
        pd.setDetail("One or more fields failed validation");
        pd.setType(URI.create("https://competitorintel.com/errors/validation"));
        pd.setProperty("fieldErrors", fieldErrors);
        pd.setProperty("timestamp", LocalDateTime.now().toString());
        pd.setProperty("path", req.getDescription(false).replace("uri=", ""));
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex, WebRequest req) {
        return problem(HttpStatus.FORBIDDEN, "You do not have permission to perform this action.", req);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(BadCredentialsException ex, WebRequest req) {
        return problem(HttpStatus.UNAUTHORIZED, "Invalid username or password.", req);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ProblemDetail> handleDisabled(DisabledException ex, WebRequest req) {
        return problem(HttpStatus.UNAUTHORIZED, "Account is disabled.", req);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ProblemDetail> handleLocked(LockedException ex, WebRequest req) {
        return problem(HttpStatus.UNAUTHORIZED, "Account is locked. Try again later.", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex, WebRequest req) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.", req);
    }

    // ------------------------------------------------------------------ //

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String detail, WebRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setDetail(detail);
        pd.setType(URI.create("https://competitorintel.com/errors/" + status.value()));
        pd.setProperty("timestamp", LocalDateTime.now().toString());
        pd.setProperty("path", req.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(status).body(pd);
    }
}
