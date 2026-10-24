package com.mldtech.nilapp.api.payout_batches.repository;

import com.mldtech.nilapp.api.payout_batches.model.PayoutBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PayoutBatchRepository extends JpaRepository<PayoutBatch, Long> {

    /**
     * Locates a payout tracking batch using its mapped parent goal instance identifier.
     * Used to advance records through the DRAFT -> PROCESSING -> SETTLED pipeline lifecycle.
     *
     * @param goalInstanceId the primary identifier of the parent campaign instance
     * @return an {@link Optional} containing the matching batch tracking entity if found
     */
    Optional<PayoutBatch> findByGoalInstanceId(Long goalInstanceId);

    /**
     * Scans and aggregates un-payout data for a closed goal instance challenge.
     * Computes exact revenue totals, sum of coins, and sum of steps to display on audit screen panels.
     */
    @Query(value = """
        SELECT 
            gi.goal_instance_id AS goalInstanceId,
            gi.entity_id AS entityId,
            e.name AS entityName,
            COALESCE(SUM(es.steps), 0)\\:\\:bigint AS totalSteps,
            COALESCE(SUM(es.coins), 0)\\:\\:bigint AS totalCoins,
            COALESCE(SUM(es.revenue_generated), 0.000000)\\:\\:decimal(15,6) AS potentialPayoutAmount,
            COALESCE(SUM(es.company_profit_retained), 0.000000)\\:\\:decimal(15,6) AS companyProfitMargin
        FROM goal_instances gi
        JOIN entities e ON gi.entity_id = e.entity_id
        LEFT JOIN entity_stats es ON es.entity_id = gi.entity_id AND es.created_at BETWEEN gi.start_date AND gi.end_date
        WHERE gi.goal_instance_id = :goalInstanceId 
          AND gi.pacing_status = 'CLOSED_PENDING_AUDIT'
        GROUP BY gi.goal_instance_id, gi.entity_id, e.name
        """, nativeQuery = true)
    Map<String, Object> calculatePotentialGoalPayoutMetrics(@Param("goalInstanceId") Long goalInstanceId);

    /**
     * Stams the unique generated payout tracking batch link back into the contribution ledger.
     * Prevents double payouts by locking rows forever.
     */
    @Modifying
    @Query(value = """
        UPDATE contributions
        SET payout_batch_id = :payoutBatchId
        WHERE entity_id = :entityId
          AND contribution_status_id = 2
          AND payout_batch_id IS NULL
          AND created_at BETWEEN :startDate AND :endDate
        """, nativeQuery = true)
    int bindContributionsToPayoutBatch(
            @Param("payoutBatchId") Long payoutBatchId,
            @Param("entityId") Long entityId,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate
    );

    /**
     * Scans for completed entity goal instances and auto-generates default payout rows.
     * Sets the recommended payout target date to exactly 60 days post-completion
     * to protect corporate cash flow and align with ad network clearing timelines.
     *
     * @return the number of default payout batch headers successfully initialized
     */
    @Modifying
    @Query(value = """
        INSERT INTO payout_batches (
            goal_instance_id, entity_id, amount_paid, total_coins_processed, 
            total_claimed_steps_processed, payout_status, payment_method, transaction_ref, 
            period_start, period_end, s3_manifest_url, recommended_payout_date, updated_at
        )
        SELECT 
            gi.goal_instance_id,
            gi.entity_id,
            gi.dollar_amount,
            gi.coins_contributed,
            gi.steps_contributed,
            'PENDING_FINANCE_RELEASE',
            'ACH',
            'AUTO_GEN_' || gi.goal_instance_id || '_' || TO_CHAR(NOW(), 'YYYYMMDD'),
            gi.start_date,
            gi.end_date,
            's3://nil-app-settlements/manifests/goal-' || gi.goal_instance_id || '-audit.json',
             -- Secure Cash-Flow Guardrail: Calculate payment target as 60 days post-completion
            (gi.completed_at\\:\\:date + INTERVAL '60 days')\\:\\:date,
            -- SEED UPDATED_AT METRIC:
            NOW()
        FROM goal_instances gi
        LEFT JOIN payout_batches pb ON pb.goal_instance_id = gi.goal_instance_id
        WHERE gi.pacing_status = 'CLOSED_PENDING_AUDIT'
          AND gi.entity_id IS NOT NULL
          AND pb.payout_batch_id IS NULL;
        """, nativeQuery = true)
    int autoInitializeDefaultPayoutBatches();



    /**
     * Retrieves all pending payout batches that have reached or passed their recommended cash-clearing date.
     * Used by the administration panel to identify disbursements ready for release.
     *
     * @return a list of PayoutBatch entities awaiting financial release
     */
    @Query(value = """
        SELECT * FROM payout_batches 
        WHERE payout_status = 'PENDING_FINANCE_RELEASE' 
          AND recommended_payout_date <= CURRENT_DATE
        ORDER BY recommended_payout_date ASC
        """, nativeQuery = true)
    List<PayoutBatch> findMaturedPendingPayouts();


}

