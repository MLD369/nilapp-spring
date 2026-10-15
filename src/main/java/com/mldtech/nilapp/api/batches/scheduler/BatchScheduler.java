package com.mldtech.nilapp.api.batches.scheduler;

import com.mldtech.nilapp.api.batches.service.BatchService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduler {

    private final BatchService batchService;

    public BatchScheduler(BatchService batchService) {
        this.batchService = batchService;
    }

    // Runs automatically every hour on the hour
//    @Scheduled(cron = "0 0 * * * *")
    // 300,000 milliseconds = 5 minutes
    @Scheduled(fixedRate = 300000)
    public void runScheduledAllocation() {
        batchService.executeContributionsAllocationBatch();
    }
}

