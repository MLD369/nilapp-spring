package com.mldtech.nilapp.exceptions;

import com.mldtech.nilapp.RequestLoggingFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

@ControllerAdvice
@Slf4j
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

    /**
     * Catches and formats errors when a user attempts a claim with no Entity selected.
     */
    @ExceptionHandler(MandatoryEntityMissingException.class)
    public ResponseEntity<Map<String, Object>> handleMissingMandatoryEntity(MandatoryEntityMissingException ex) {
        log.warn("Guard Gate Intercepted Structural Error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getErrorDetails());
    }

    /**
     * Catches and formats errors when a user selects a closed/completed campaign target.
     */
    @ExceptionHandler(ClosedAllocationException.class)
    public ResponseEntity<Map<String, Object>> handleClosedAllocations(ClosedAllocationException ex) {
        log.warn("Guard Gate Intercepted Business Rule Error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getErrorDetails());
    }

    /**
     * Catches and formats errors when user allocation percentages or goal shares fail to equal 100%.
     */
    @ExceptionHandler(InvalidAllocationMathException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAllocationMath(InvalidAllocationMathException ex) {
        log.warn("Guard Gate Intercepted Mathematical Imbalance: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getErrorDetails());
    }
}
