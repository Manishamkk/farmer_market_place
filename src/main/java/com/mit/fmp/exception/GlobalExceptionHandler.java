package com.mit.fmp.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle Users Not Found Exception
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(
            UserNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Users Not Found");
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(
                response, HttpStatus.NOT_FOUND);
    }

    // Handle Product Not Found (including existing ResponseStatusException)
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            org.springframework.web.server.ResponseStatusException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", ex.getStatusCode().value());
        response.put("error", "Request Error");
        response.put("message", ex.getReason());

        return ResponseEntity.status(ex.getStatusCode())
                .body(response);
    }

    // Handle validation errors from @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        response.put("messages", errors);

        return ResponseEntity.badRequest().body(response);
    }



@ExceptionHandler(OrderNotFoundException.class)
public ResponseEntity<Map<String, String>> handleOrderNotFound(
        OrderNotFoundException ex) {

    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(response);
}

    
@ExceptionHandler(ProductNotFoundException.class)
public ResponseEntity<Map<String, Object>> handleProductNotFound(
        ProductNotFoundException ex) {

    Map<String, Object> response = new HashMap<>();

    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.NOT_FOUND.value());
    response.put("error", "Product Not Found");
    response.put("message", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(response);
}

    
    
    // Handle other unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "Something went wrong");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
