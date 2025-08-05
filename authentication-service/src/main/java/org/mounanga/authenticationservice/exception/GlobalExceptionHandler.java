package org.mounanga.authenticationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotEnabledException.class)
    public ResponseEntity<Map<String, String>> handleUserNotEnabled(UserNotEnabledException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "UserNotEnabledException");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN); // 403 Forbidden
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<Map<String, String>> handleUserNotAuthenticated(UserNotAuthenticatedException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "UserNotAuthenticatedException");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
    }

    // Other exceptions
}