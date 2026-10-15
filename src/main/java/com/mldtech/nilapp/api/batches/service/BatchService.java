package com.mldtech.nilapp.api.batches.service;

import com.mldtech.nilapp.api.contributions.repository.ContributionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchService {

    private static final Logger log = LoggerFactory.getLogger(BatchService.class);
    private final ContributionRepository contributionRepository;

    public BatchService(ContributionRepository contributionRepository) {
        this.contributionRepository = contributionRepository;
    }


    @Transactional
    public int executeContributionsAllocationBatch() {
        log.info("Starting master metrics evaluation and allocation batch pipeline...");
        try {
            // 1. Run Entity Goal evaluation
            int entityGoals = contributionRepository.evaluateEntityGoals();
            log.info("Pipeline Step 1 Complete: Entity goals updated. Impacted rows: {}", entityGoals);

            // 2. Run Group Goal evaluation
            int groupGoals = contributionRepository.evaluateGroupGoals();
            log.info("Pipeline Step 2 Complete: Group goals updated. Impacted rows: {}", groupGoals);

            // 3. sync individual contract rows right after parent instances update
            int userGoalsFinalized = contributionRepository.finalizeUserGoalsFromInstances();
            log.info("Pipeline Step 3 Complete: User goals updated. Impacted rows: {}", userGoalsFinalized);

            // 4. Process, unnest JSON arrays, and move records into entity_stats and group_stats
            int rowsUpdated = contributionRepository.processPendingContributions();
            log.info("Pipeline Step 4 Complete: Processed master records and flipped status: {}", rowsUpdated);

            // 5. Check and grant achievements based on updated statistics
            if (rowsUpdated > 0) {
                int achievementsEarned = contributionRepository.assignUserAchievements();
                log.info("Pipeline Step 5 Complete: User achievements evaluated. Unlocked: {}", achievementsEarned);
            }

            return rowsUpdated;
        } catch (Exception e) {
            log.error("Batch processing pipeline failed. All operations rolled back structural states automatically.", e);
            throw e;
        }
    }

}

