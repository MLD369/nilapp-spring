package com.mldtech.nilapp.exceptions;

import lombok.Getter;

/**
 * Exception thrown when an incoming contribution allocation snapshot attempts to route
 * steps or coins toward one or more goal instances that are already completed, expired,
 * or deactivated.
 */
@Getter
public class ClosedAllocationException extends RuntimeException {

    private final ContributionErrorPayload errorDetails;
    private final int totalClosedTargetsDetected;

    /**
     * Constructs a descriptive business compliance validation exception.
     *
     * @param userId       the identifier of the submitting user
     * @param closedCount  cumulative number of deactivated or completed targets caught inside the claim snapshot
     * @param errorDetails structured {@link ContributionErrorPayload} metadata containing item names, step calculations, and coins
     */
    public ClosedAllocationException(
            Long userId,
            int closedCount,
            ContributionErrorPayload errorDetails
    ) {
        super(String.format("[Business Compliance Violation] Ingestion blocked for User ID %d. " +
                        "Detected %d inactive/completed campaign target(s) inside allocationSnapshot. Re-allocation required.",
                userId, closedCount));
        this.totalClosedTargetsDetected = closedCount;
        this.errorDetails = errorDetails;
    }
}


