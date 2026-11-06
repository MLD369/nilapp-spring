package com.mldtech.nilapp.api.batches.scheduler;

import com.mldtech.nilapp.api.batches.service.BatchService;
import com.mldtech.nilapp.api.payout_batches.service.PayoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduler {

    private static final Logger log = LoggerFactory.getLogger(BatchScheduler.class);

    private final BatchService batchService;
    private final PayoutService payoutService;

    public BatchScheduler(BatchService batchService, PayoutService payoutService) {
        this.batchService = batchService;
        this.payoutService = payoutService;
    }

    // Runs automatically every hour on the hour
//    @Scheduled(cron = "0 0 * * * *")
    // 300,000 milliseconds = 5 minutes
    @Scheduled(fixedRate = 300000)
    public void runScheduledAllocation() {
//        batchService.executeContributionsAllocationBatch();
//        TODO this is just for running locally
    }

    /**
     * Main Metrics, Allocation splits, Pacing Calibrations, and Achievements evaluation loops.
     * Configured to run automatically every 6 hours. @Scheduled(cron = "0 0 * * * *") for hourly
     */
//    @Scheduled(cron = "0 0 */6 * * *")
    @Scheduled(fixedRate = 300000)
    public void runScheduledMetricsPipeline() {
        log.info("runScheduledMetricsPipeline test");
        log.info("runScheduledMetricsPipeline test");
        log.info("runScheduledMetricsPipeline");
        log.info("runScheduledMetricsPipeline");
        log.info("Executing 6-hour metric fan-out and goal tracking compilation worker...");
        try {
            batchService.executeContributionsAllocationBatch(); // TODO turn on
        } catch (Exception e) {
            log.error("Scheduled metrics optimization pipeline encountered an evaluation crash.", e);
        }
    }

    /**
     * Financial Settlements Monitoring loop. Catches newly closed entity goals
     * and provisions default payout batch headers automatically.
     * Configured to follow the 6-hour core calculation layer window.
     */
    @Scheduled(cron = "0 5 */6 * * *") // Runs 5 minutes past the hour every 6 hours
    public void runScheduledPayoutHeaderDiscovery() {
        log.info("runScheduledPayoutHeaderDiscovery");
        log.info("runScheduledPayoutHeaderDiscovery");
        log.info("runScheduledPayoutHeaderDiscovery");
        log.info("runScheduledPayoutHeaderDiscovery");
        log.info("Scanning for closed goals to auto-generate default payout headers...");
        try {
            int initializedHeaders = payoutService.runAutoPayoutInitializationPipeline();
            if (initializedHeaders > 0) {
                log.info("Successfully provisioned {} default payout entries in PENDING_FINANCE_RELEASE status.", initializedHeaders);
            }
        } catch (Exception e) {
            log.error("Automated financial payout initialization worker encountered a mapping exception.", e);
        }
    }

    /**
     * Orchestrates daily user activity summarization by wrapping up yesterday's workout metrics
     * and safely shifting them to lifetime account metrics via sequential locking keys.
     * Execution Schedule: Runs once a day at 12:05 AM (00:05) local system time.
     */
//    @Scheduled(cron = "0 5 0 * * *")
    @Scheduled(fixedRate = 400000)
    public void runDailyWorkoutMetricsAndProfileSyncBatch() {
        log.info("runDailyWorkoutMetricsAndProfileSyncBatch test");
        log.info("runDailyWorkoutMetricsAndProfileSyncBatch test");
        log.info("runDailyWorkoutMetricsAndProfileSyncBatch");
        log.info("runDailyWorkoutMetricsAndProfileSyncBatch");

        log.info("Cron Trigger: Initiating daily activity rollup and user profile synchronization.");
        try {
            batchService.executeDailyStatsAggregationBatch();// TODO turn on
            log.info("Cron Trigger Success: Daily workout logistics summary processing pipeline completed successfully.");
        } catch (Exception e) {
            log.error("Cron Trigger Failure: Critical issue caught during daily workout metric aggregation loop.", e);
        }
    }
}

