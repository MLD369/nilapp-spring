package com.mldtech.nilapp.enums;

import lombok.Getter;

@Getter
public enum PayoutBatchStatusEnum {

    DRAFT(
            "Created (Waiting for clearing date)",
            "Default initial payout batch state upon creation"
    ),
    PENDING_FINANCE_RELEASE(
            "Ready for manager release",
            "Reached or passed recommended cash-clearing date and awaiting financial release"
    ),
    PROCESSING(
            "Manager approved & generating file",
            "Manager has initiated payout file generation"
    ),
    SETTLED(
            "It has been paid",
            "Finalized with payment tracking IDs and disbursement completed"
    ),
    FAILED(
            "Failed processing",
            "Payout batch processing failed and requires review"
    );

    private final String laymanDescription;
    private final String description;

    PayoutBatchStatusEnum(String laymanDescription, String description) {
        this.laymanDescription = laymanDescription;
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
