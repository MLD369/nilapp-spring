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
        log.info("Starting master metrics evaluation and financial allocation batch pipeline...");

        try {
            // 1. Process and Flatten Ingestion Layers (Calculates Entity Revenue & Platform Profit)
            int rowsUpdated = contributionRepository.processPendingContributions();

            // 2. Evaluate Entity Campaigns (Updates coins, steps, real dollar totals, and handles status transformations)
            int entityGoals = contributionRepository.evaluateEntityGoals();

            // 3. Evaluate Group Campaigns (Pure gamification - tracks steps and coins ONLY)
            int groupGoals = contributionRepository.evaluateGroupGoals();

            // 4. Update pacing status indicators across all active initiatives
            int pacingUpdates = contributionRepository.recalculateGoalPacingStatuses();

            // 5. Sync individual contract rows right after parent instances update
            int userGoalsFinalized = contributionRepository.finalizeUserGoalsFromInstances();

            // 6. Evaluate individual user-driven goals if master records were modified
            int individualUserGoalsProcessed = 0;
            int achievementsEarned = 0;

            if (rowsUpdated > 0) {
                individualUserGoalsProcessed = contributionRepository.evaluateUserGoalsBatch();
                achievementsEarned = contributionRepository.assignUserAchievements();
            }

            // Consolidated summary analytics log block prevents raw SQL lines from cluttering output
            // Consolidated summary analytics log block tells you exactly what closed or updated
            log.info("""
                
                ======================================================================
                📈 REVENUE-ALIGNED BATCH PIPELINE SUCCESS SUMMARY
                ======================================================================
                🔹 Pending Ad Claims Approved   -> [ {} records processed ]
                🔹 Entity Financial + Goals       -> [ {} goals updated ]
                🔹 Group Social Goals           -> [ {} goals evaluated ]
                🔹 Campaign Pacing Adjusted     -> [ {} statuses calibrated ]
                🔹 Team Goals Closed & Synced   -> [ {} user_goals closed ]
                🔹 Individual User Goals Closed -> [ {} goals closed & archived ]
                🔹 Accolades & Badges Minted     -> [ {} achievements earned ]
                ======================================================================
                """,
                    rowsUpdated,
                    entityGoals,
                    groupGoals,
                    pacingUpdates,
                    userGoalsFinalized,
                    individualUserGoalsProcessed,
                    achievementsEarned
            );


            return rowsUpdated;

        } catch (Exception e) {
            log.error("Batch processing pipeline failed. All structural and financial changes rolled back automatically.", e);
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
