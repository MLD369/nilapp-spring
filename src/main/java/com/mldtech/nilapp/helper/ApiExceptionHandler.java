package com.mldtech.nilapp.helper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomResponse<?>> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .badRequest()
                .body(new CustomResponse<>(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST,
                        "400"
                ));
    }
}

