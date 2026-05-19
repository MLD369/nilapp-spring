package com.mldtech.nilapp.api.contributions.children.ContributionStatus.helper

object ContributionStatusHelper {
    // Status IDs
    const val PENDING: Int = 1
    const val VERIFIED: Int = 2
    const val PAID: Int = 3
    const val REJECTED: Int = 4

    // Status Codes
    const val CODE_PENDING: String = "PENDING"
    const val CODE_VERIFIED: String = "VERIFIED"
    const val CODE_PAID: String = "PAID"
    const val CODE_REJECTED: String = "REJECTED"

    // Status Labels
    const val LABEL_PENDING: String = "Pending"
    const val LABEL_VERIFIED: String = "Verified"
    const val LABEL_PAID: String = "Paid"
    const val LABEL_REJECTED: String = "Rejected"

    // Status Descriptions
    const val DESC_PENDING: String = "Contribution is pending verification"
    const val DESC_VERIFIED: String = "Contribution has been verified"
    const val DESC_PAID: String = "Contribution has been paid to entity"
    const val DESC_REJECTED: String = "Contribution is rejected"
}
