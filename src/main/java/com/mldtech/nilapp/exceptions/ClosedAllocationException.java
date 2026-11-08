package com.mldtech.nilapp.exceptions;

import lombok.Getter;
import java.util.Map;

@Getter
public class ClosedAllocationException extends RuntimeException {
    private final Map<String, Object> errorDetails;
    private final int totalClosedTargetsDetected;

    /**
     * Constructs a descriptive business compliance validation exception.
     *
     * @param userId the identifier of the submitting user
     * @param closedCount cumulative number of deactivated targets caught inside the claim snapshot
     * @param errorDetails structured payload metadata containing item names, step calculations, and coins
     */
    public ClosedAllocationException(Long userId, int closedCount, Map<String, Object> errorDetails) {
        super(String.format("[Business Compliance Violation] Ingestion blocked for User ID %d. " +
                        "Detected %d inactive/completed campaign target(s) inside allocationSnapshot. Re-allocation required.",
                userId, closedCount));
        this.totalClosedTargetsDetected = closedCount;
        this.errorDetails = errorDetails;
    }
}

