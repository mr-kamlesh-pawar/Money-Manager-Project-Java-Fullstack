package com.kp.moneyManager.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle JWT-related exceptions
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<String> handleJwtException(JwtAuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }


    //Email alredy present
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        ErrorResponse error = ErrorResponse.builder(ex, HttpStatus.CONFLICT, ex.getMessage())
                .title("Registration Failed")
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }


}