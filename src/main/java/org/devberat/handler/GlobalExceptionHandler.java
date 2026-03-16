package org.devberat.handler;

import io.jsonwebtoken.ExpiredJwtException;
import org.devberat.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ApiError<String>> handleBaseException(BaseException exception, WebRequest request){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createApiError(exception.getMessage(), request));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError<String>> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createApiError("Invalid email or password.", request));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError<String>> handleDisabledException(DisabledException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createApiError("This account is currently inactive.", request));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiError<String>> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createApiError("Your session has expired. Please log in again.", request));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError<String>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createApiError("Access Denied: You must be an ADMIN to perform this operation.", request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError<String>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createApiError("Validation failed: " + errors, request));
    }

    @ExceptionHandler(java.lang.Exception.class)
    public ResponseEntity<ApiError<String>> handleGeneralException(java.lang.Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createApiError("An unexpected system error occurred: " + ex.getMessage(), request));
    }
    private <E> ApiError<E> createApiError(E message, WebRequest request) {
        ApiError<E> apiError = new ApiError<>();
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        org.devberat.handler.Exception<E> exception = new org.devberat.handler.Exception<>();
        exception.setCreateTime(new Date());
        exception.setPath(request.getDescription(false).substring(4));
        exception.setMessage(message);
        apiError.setException(exception);
        return apiError;
    }
}