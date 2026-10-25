package com.mldtech.nilapp.api.payout_batches.service;

import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import com.mldtech.nilapp.api.goals.children.GoalInstances.model.GoalInstance;
import com.mldtech.nilapp.api.goals.children.GoalInstances.repository.GoalInstanceRepository;
import com.mldtech.nilapp.api.payout_batches.model.PayoutBatch;
import com.mldtech.nilapp.api.payout_batches.repository.PayoutBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PayoutService {

    private final PayoutBatchRepository payoutBatchRepository;
    private final ContributionRepository contributionRepository;
    private final GoalInstanceRepository goalInstanceRepository;

    public PayoutService(PayoutBatchRepository payoutBatchRepository, ContributionRepository contributionRepository, GoalInstanceRepository goalInstanceRepository) {
        this.payoutBatchRepository = payoutBatchRepository;
        this.contributionRepository = contributionRepository;
        this.goalInstanceRepository = goalInstanceRepository;
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
     * Drops an unfinalized record into the ledger database layer, defaulting to DRAFT.
     */
    @Transactional
    public PayoutBatch createInitialPayoutDraft(Long goalInstanceId) {
        Map<String, Object> preview = getPayoutPreviewMetrics(goalInstanceId);
        Long entityId = (Long) preview.get("entityid");
        BigDecimal amountPaid = (BigDecimal) preview.get("potentialpayoutamount");
        Long totalSteps = (Long) preview.get("totalsteps");
        Long totalCoins = (Long) preview.get("totalcoins");

        GoalInstance goalInstance = goalInstanceRepository.findById(goalInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("Goal Instance not found"));

        PayoutBatch draftBatch = PayoutBatch.builder()
                .entityId(entityId)
                .goalInstanceId(goalInstanceId)
                .amountPaid(amountPaid)
                .totalClaimedStepsProcessed(totalSteps)
                .totalCoinsProcessed(totalCoins)
                .paymentMethod("ACH")
                .trackingNumber("TRACE_PENDING_AUDIT")
                .payoutStatus("DRAFT") // Default state
                .periodStart(goalInstance.getStartDate())
                .periodEnd(goalInstance.getEndDate())
                .transactionRef("TX_DRAFT_" + goalInstanceId + "_" + System.currentTimeMillis())
                .s3ManifestUrl("s3://nil-app-settlements/manifests/draft-goal-" + goalInstanceId + ".json")
                .updatedAt(java.time.LocalDateTime.now())
                .recommendedPayoutDate(goalInstance.getEndDate().plusDays(60))
                .build();

        return payoutBatchRepository.save(draftBatch);
    }

    /**
     * Fallback tracking generator called when internal calculation exceptions arise.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordProcessingFailure(Long goalInstanceId, String errorMessage) {
        PayoutBatch failedLog = PayoutBatch.builder()
                .goalInstanceId(goalInstanceId)
                .amountPaid(BigDecimal.ZERO)
                .paymentMethod("NONE")
                .trackingNumber("ERROR_LOGGED")
                .payoutStatus("FAILED") // Captured exception status
                .transactionRef("ERR_" + goalInstanceId + "_" + System.currentTimeMillis())
                .s3ManifestUrl("none")
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        payoutBatchRepository.save(failedLog);
    }

    /**
     * Transitions a record from DRAFT to PROCESSING when a manager begins file generation.
     */
    @Transactional
    public PayoutBatch transitionToProcessing(Long goalInstanceId) {
        PayoutBatch batch = payoutBatchRepository.findByGoalInstanceId(goalInstanceId)
                .orElseThrow(() -> new IllegalStateException("No payout record found for this instance."));

        if (!"DRAFT".equals(batch.getPayoutStatus())) {
            throw new IllegalStateException("Cannot process payout. Status must be DRAFT but was: " + batch.getPayoutStatus());
        }

        batch.setPayoutStatus("PROCESSING");
        batch.setUpdatedAt(java.time.LocalDateTime.now());
        return payoutBatchRepository.save(batch);
    }

    /**
     * Finalizes the record with payment tracking IDs and updates its state to SETTLED.
     */
    @Transactional
    public PayoutBatch executeFinalSettlement(Long goalInstanceId, String paymentMethod, String trackingNumber) {
        PayoutBatch batch = payoutBatchRepository.findByGoalInstanceId(goalInstanceId)
                .orElseThrow(() -> new IllegalStateException("No active payout record found for execution."));

        if (!"PROCESSING".equals(batch.getPayoutStatus())) {
            throw new IllegalStateException("Cannot settle payout. Status must be PROCESSING to authorize wire clearing.");
        }

        batch.setPaymentMethod(paymentMethod);
        batch.setTrackingNumber(trackingNumber);
        batch.setPayoutStatus("SETTLED");
        batch.setUpdatedAt(java.time.LocalDateTime.now());

        // Safely updates the contribution tables to PAID now that the transaction has been verified
        contributionRepository.finalizeContributionsToPaid(goalInstanceId, batch.getPayoutBatchId());

        // Pull the goal_instance out of the active audit loop completely
        contributionRepository.updatePacingStatusByCode(goalInstanceId, "SETTLED");

        return payoutBatchRepository.save(batch);
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

