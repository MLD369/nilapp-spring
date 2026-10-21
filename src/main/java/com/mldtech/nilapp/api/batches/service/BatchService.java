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

            // 2. Run Group Goal evaluation
            int groupGoals = contributionRepository.evaluateGroupGoals();

            // 3. Sync individual contract rows right after parent instances update
            int userGoalsFinalized = contributionRepository.finalizeUserGoalsFromInstances();

            // 4. Process, unnest JSON arrays, and move records into entity_stats and group_stats
            int rowsUpdated = contributionRepository.processPendingContributions();

            // 5. Evaluate individual user-driven goals if master records were modified
            int individualUserGoalsProcessed = 0;
            int achievementsEarned = 0;

            if (rowsUpdated > 0) {
                individualUserGoalsProcessed = contributionRepository.evaluateUserGoalsBatch();
                achievementsEarned = contributionRepository.assignUserAchievements();
            }

            // Consolidated summary log block prevents raw SQL lines from cluttering your output
            log.info("""
                
                ======================================================================
                📈 BATCH PROCESSING PIPELINE COMPLETE SUMMARY
                ======================================================================
                🔹 Step 1: Entity Goals Updated        -> [ {} rows ]
                🔹 Step 2: Group Goals Updated         -> [ {} rows ]
                🔹 Step 3: User Goals Finalized        -> [ {} rows ]
                🔹 Step 4: Master Ingestions Approved Entity/Group stats   -> [ {} rows ]
                🔹 Step 5: User Tiers Evaluated        -> [ {} rows ]
                🔹 Step 6: Non-Free-Rider Medals/Achievements Earned -> [ {} badges ]
                ======================================================================
                """,
                    entityGoals,
                    groupGoals,
                    userGoalsFinalized,
                    rowsUpdated,
                    individualUserGoalsProcessed,
                    achievementsEarned
            );

            return rowsUpdated;

        } catch (Exception e) {
            log.error("Batch processing pipeline failed. All operations rolled back structural states automatically.", e);
            throw e;
        }
    }
}
/*

API to allocate claims to groups and entities - done
change contributions - done

Todo	Api to check entity goals - done
Todo	Api to check group goals - done
Todo	Api to assign achievements - done


Todo	Notification that goal or achievement reached (ui maybe when user opens app or push notificatookmn)


    Api to save total steps - once a day when user opens app - done - TODO ui
	Api to save user streak - once a day when user opens app - done - todo ui
	shield /streak saver
  */
