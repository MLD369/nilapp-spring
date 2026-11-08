package com.mldtech.nilapp.exceptions;

import lombok.Getter;
import java.util.Map;

@Getter
public class InvalidAllocationMathException extends RuntimeException {
    private final Map<String, Object> errorDetails;
    private final String mathematicalScope;

    /**
     * Constructs a descriptive mathematical balancing exception.
     *
     * @param userId the identifier of the submitting user
     * @param scope where the imbalance happened ('ENTITIES', 'GROUPS', 'GOAL_SHARES')
     * @param calculatedSum the sum that broke the 100% validation check
     * @param errorDetails structured metadata map sent straight back to the UI
     */
    public InvalidAllocationMathException(Long userId, String scope, double calculatedSum, Map<String, Object> errorDetails) {
        super(String.format("[Mathematical Balance Violation] Ingestion blocked for User ID %d. " +
                        "Allocation scope '%s' summed up to %.2f instead of the mandatory target.",
                userId, scope, calculatedSum));
        this.mathematicalScope = scope;
        this.errorDetails = errorDetails;
    }
}

