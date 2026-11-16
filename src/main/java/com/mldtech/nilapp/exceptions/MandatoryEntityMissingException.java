package com.mldtech.nilapp.exceptions;

import lombok.Getter;

/**
 * Exception thrown when an incoming contribution allocation payload violates mandatory structural
 * presence or weighting rules (e.g., missing entity allocations, empty selection arrays, or
 * percentage allocations lacking attached target goals).
 */
@Getter
public class MandatoryEntityMissingException extends RuntimeException {

    private final ContributionErrorPayload errorDetails;
    private final String violationReasonCode;

    /**
     * Constructs a descriptive structural validation exception.
     *
     * @param userId             the identifier of the submitting user
     * @param reasonCode         an internal categorization token (e.g., 'MISSING_ENTITY_ARRAY', 'EMPTY_ENTITY_LIST', 'ORPHANED_ENTITY_WEIGHT')
     * @param explanatoryMessage specific operational details for logging engines
     * @param errorDetails       structured {@link ContributionErrorPayload} metadata passed back to the UI interface
     */
    public MandatoryEntityMissingException(
            Long userId,
            String reasonCode,
            String explanatoryMessage,
            ContributionErrorPayload errorDetails
    ) {
        super(String.format("[Structural Allocation Violation] User ID %d failed rule check '%s': %s",
                userId, reasonCode, explanatoryMessage));
        this.violationReasonCode = reasonCode;
        this.errorDetails = errorDetails;
    }
}