package com.mldtech.nilapp.api.payout_batches.service;

import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.payout_batches.model.PayoutBatch;
import com.mldtech.nilapp.api.payout_batches.repository.PayoutBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PayoutService {

    private final PayoutBatchRepository payoutBatchRepository;
    private final ContributionRepository contributionRepository;

    public PayoutService(PayoutBatchRepository payoutBatchRepository, ContributionRepository contributionRepository) {
        this.payoutBatchRepository = payoutBatchRepository;
        this.contributionRepository = contributionRepository;
    }

    /**
     * Fetches exact summary calculations to let admins preview total coins, steps, and revenue before executing.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPayoutPreviewMetrics(Long goalInstanceId) {
        Map<String, Object> metrics = payoutBatchRepository.calculatePotentialGoalPayoutMetrics(goalInstanceId);
        if (metrics.isEmpty()) {
            throw new IllegalArgumentException("No auditable goal challenge records found matching ID: " + goalInstanceId);
        }
        return metrics;
    }

    /**
     * Processes financial clear closures. Creates the payout sheet, links contributions, and advances state.
     */
    @Transactional
    public PayoutBatch executeFinancialSettlement(Long goalInstanceId, String paymentMethod, String trackingNumber) {
        Map<String, Object> metrics = payoutBatchRepository.calculatePotentialGoalPayoutMetrics(goalInstanceId);
        if (metrics.isEmpty()) {
            throw new IllegalStateException("Goal challenge is not in CLOSED_PENDING_AUDIT state.");
        }

        Long entityId = ((Number) metrics.get("entityid")).longValue();
        BigDecimal amountPaid = new BigDecimal(metrics.get("potentialpayoutamount").toString());
        Long totalSteps = ((Number) metrics.get("totalsteps")).longValue();
        Long totalCoins = ((Number) metrics.get("totalcoins")).longValue();

        // 1. Initialize complete structural record row
        PayoutBatch batch = PayoutBatch.builder()
                .entityId(entityId)
                .goalInstanceId(goalInstanceId)
                .amountPaid(amountPaid)
                .totalClaimedStepsProcessed(totalSteps)
                .totalCoinsProcessed(totalCoins)
                .paymentMethod(paymentMethod)
                .trackingNumber(trackingNumber)
                .payoutStatus("SETTLED")
                .periodStart(LocalDate.now().minusDays(30)) // Adjust dynamically as required
                .periodEnd(LocalDate.now())
                .transactionRef("TX_REF_" + goalInstanceId + "_" + System.currentTimeMillis())
                .s3ManifestUrl("s3://nil-app-settlements/manifests/goal-" + goalInstanceId + ".json")
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        batch = payoutBatchRepository.save(batch);

        // 2. Lock matching transactional rows forever
        payoutBatchRepository.bindContributionsToPayoutBatch(
                batch.getPayoutBatchId(), entityId, batch.getPeriodStart(), batch.getPeriodEnd()
        );

        // 3. Mark master campaign row as fully SETTLED inside the DB catalog
        // Assuming a native mapping or update loop execution here to turn pacing_status to 'SETTLED'

        return batch;
    }
    /**
     * Scans closed campaign instances and auto-generates baseline draft records
     * inside the payout registry for your financial management teams to view.
     */
    @Transactional
    public int runAutoPayoutInitializationPipeline() {
        return payoutBatchRepository.autoInitializeDefaultPayoutBatches();
    }


    /**
     * Fetches a list of all entity payout batches that have passed their 60-day cash clearing window
     * and are safely ready for corporate fund release.
     *
     * @return a list of data models ready for treasury disbursal
     */
    @Transactional(readOnly = true)
    public List<PayoutBatch> getMaturedPendingPayouts() {
        return payoutBatchRepository.findMaturedPendingPayouts();
    }

}

