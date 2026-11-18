package com.mldtech.nilapp.enums;

import lombok.Getter;

@Getter
public enum PaymentMethodEnum {

    ACH(
            "Direct Bank Deposit (ACH)",
            "Automated Clearing House electronic bank-to-bank transfer"
    ),
    WIRE(
            "Bank Wire Transfer",
            "Same-day or international direct wire disbursement"
    ),
    CHECK(
            "Physical Mailed Check",
            "Printed paper check mailed to the organization address"
    ),
    ZELLE(
            "Zelle Instant Transfer",
            "Direct email or phone-linked bank transfer via Zelle"
    ),
    PAYPAL(
            "PayPal Payout",
            "Electronic disbursement sent to a verified PayPal business account"
    ),
    STRIPE_CONNECT(
            "Stripe Direct Payout",
            "Automated platform transfer via Stripe Connect"
    ),
    VIRTUAL_CARD(
            "Virtual Prepaid Card",
            "Digital single-use or reloadable corporate disbursement card"
    );

    private final String laymanDescription;
    private final String description;

    PaymentMethodEnum(String laymanDescription, String description) {
        this.laymanDescription = laymanDescription;
        this.description = description;
    }

    /**
     * Safely parses a case-insensitive payment method string from an API request.
     */
    public static PaymentMethodEnum fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payment method cannot be null or blank.");
        }
        try {
            return PaymentMethodEnum.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid PaymentMethodEnum value: " + value);
        }
    }
}
