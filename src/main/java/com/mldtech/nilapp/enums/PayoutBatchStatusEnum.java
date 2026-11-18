package com.mldtech.nilapp.enums;

import lombok.Getter;

@Getter
public enum PayoutBatchStatusEnum {

    DRAFT("Initial draft payout batch created and awaiting review"),
    PENDING_FINANCE_RELEASE("Approved by system and awaiting finance team release"),
    PROCESSING("Payout transfer is currently in progress"),
    SETTLED("Funds have been successfully transferred and settled"),
    FAILED("Payout processing encountered an error and requires attention");

    private final String description;

    PayoutBatchStatusEnum(String description) {
        this.description = description;
    }

    /**
     * Safely parses a case-insensitive status string from an API request or query parameter.
     */
    public static PayoutBatchStatusEnum fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payout batch status cannot be null or blank.");
        }
        try {
            return PayoutBatchStatusEnum.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid PayoutBatchStatusEnum value: " + value);
        }
    }
}
