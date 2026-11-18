package com.mldtech.nilapp.api.payout_batches.model;

import com.mldtech.nilapp.enums.PayoutBatchStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payout_batches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payout_batch_id")
    private Long payoutBatchId;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "goal_instance_id")
    private Long goalInstanceId;

    @Column(name = "amount_paid", precision = 15, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "transaction_ref", length = 100)
    private String transactionRef;

    @Column(name = "period_start")
    private LocalDate periodStart;

    @Column(name = "period_end")
    private LocalDate periodEnd;

    @Column(name = "s3_manifest_url")
    private String s3ManifestUrl;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status", nullable = false, length = 50)
    @Builder.Default
    private PayoutBatchStatusEnum payoutStatus = PayoutBatchStatusEnum.DRAFT;

    @Column(name = "total_coins_processed")
    @Builder.Default
    private Long totalCoinsProcessed = 0L;

    @Column(name = "total_claimed_steps_processed")
    @Builder.Default
    private Long totalClaimedStepsProcessed = 0L;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "recommended_payout_date")
    private LocalDate recommendedPayoutDate;

}
