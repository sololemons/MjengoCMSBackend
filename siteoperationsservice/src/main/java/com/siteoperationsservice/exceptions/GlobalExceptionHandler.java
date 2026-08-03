package com.siteoperationsservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedExceptions(UnauthorizedException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage());
    }
    @ExceptionHandler(MissingFieldException.class)
    public ResponseEntity<Map<String, Object>> handleMissingFieldException(MissingFieldException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundExceptions(UserNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found", ex.getMessage());
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage());
    }
    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handle(GeofenceViolationException ex) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Location Not acceptable", ex.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        String errorMessage = "Endpoint not found: " + ex.getRequestURL();
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DuplicateDailyLogException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateDailyLogException(DuplicateDailyLogException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Duplicate Entry", ex.getMessage());
    }


    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", Optional.of(status.value()));
        response.put("error", error);
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

}
