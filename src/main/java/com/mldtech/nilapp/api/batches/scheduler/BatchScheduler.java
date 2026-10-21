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
        batchService.executeContributionsAllocationBatch();
    }

    /**
     * Main Metrics, Allocation splits, Pacing Calibrations, and Achievements evaluation loops.
     * Configured to run automatically every 6 hours.
     */
    @Scheduled(cron = "0 0 */6 * * *")
    public void runScheduledMetricsPipeline() {
        log.info("Executing 6-hour metric fan-out and goal tracking compilation worker...");
        try {
            batchService.executeContributionsAllocationBatch();
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
}

