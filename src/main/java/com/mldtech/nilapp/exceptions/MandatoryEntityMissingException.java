package com.mldtech.nilapp.exceptions;

import lombok.Getter;
import java.util.Map;

@Getter
public class MandatoryEntityMissingException extends RuntimeException {
    private final Map<String, Object> errorDetails;
    private final String violationReasonCode;

    /**
     * Constructs a descriptive structural validation exception.
     *
     * @param userId the identifier of the submitting user
     * @param reasonCode an internal categorization token (e.g., 'MISSING_ARRAY', 'EMPTY_ALLOCATIONS', 'ORPHANED_WEIGHT')
     * @param explanatoryMessage specific operational details for logging engines
     * @param errorDetails structured payload metadata passed back to the UI interface
     */
    public MandatoryEntityMissingException(Long userId, String reasonCode, String explanatoryMessage, Map<String, Object> errorDetails) {
        super(String.format("[Structural Allocation Violation] User ID %d failed rule check '%s': %s",
                userId, reasonCode, explanatoryMessage));
        this.violationReasonCode = reasonCode;
        this.errorDetails = errorDetails;
    }
}
