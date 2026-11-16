package com.mldtech.nilapp.exceptions;

import lombok.Getter;

/**
 * Exception thrown when incoming contribution allocation percentages or goal shares fail
 * to sum up to their required mathematical target (e.g., entity splits not equaling 100%,
 * group splits not equaling 100%, or goal shares not equaling 1.0).
 */
@Getter
public class InvalidAllocationMathException extends RuntimeException {

    private final ContributionErrorPayload errorDetails;
    private final String mathematicalScope;

    /**
     * Constructs a descriptive mathematical balancing exception.
     *
     * @param userId        the identifier of the submitting user
     * @param scope         where the imbalance happened (e.g., 'ENTITY_PERCENTAGES', 'GROUP_PERCENTAGES', 'GOAL_SHARES_ENTITY_1')
     * @param calculatedSum the sum that broke the 100% validation check
     * @param errorDetails  structured {@link ContributionErrorPayload} metadata sent straight back to the UI
     */
    public InvalidAllocationMathException(
            Long userId,
            String scope,
            double calculatedSum,
            ContributionErrorPayload errorDetails
    ) {
        super(String.format("[Mathematical Balance Violation] Ingestion blocked for User ID %d. " +
                        "Allocation scope '%s' summed up to %.2f instead of the mandatory target.",
                userId, scope, calculatedSum));
        this.mathematicalScope = scope;
        this.errorDetails = errorDetails;
    }
}


