package com.mldtech.nilapp;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handle404(HttpServletRequest request, NoHandlerFoundException ex) {
        System.err.println();
        System.err.println();
        System.out.println("404 URL → " + request.getRequestURL() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        System.err.println();
        System.err.println();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Not Found");
    }
}
