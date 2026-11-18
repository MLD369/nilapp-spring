package com.mldtech.nilapp.helper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class AppConstants {
    public static final int MAX_SHIELDS = 3;

    // TODO make it so this is auto generated and decide if it should be 7s or lower or higher
    public static final List<Integer> ORDERED_MILESTONES = List.of(
            3, 7, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77, 84, 91, 98,
            105, 112, 119, 126, 133, 140, 147, 154, 161, 168, 175, 182,
            189, 196, 203, 210, 217, 224, 231, 238, 245, 252, 259, 266,
            273, 280, 287, 294, 301, 308, 315, 322, 329, 336, 343, 350, 357, 365
    );
    public static final Set<Integer> SHIELD_MILESTONES = Set.copyOf(ORDERED_MILESTONES);

    public static final Set<Integer> MILESTONE_SET = Set.copyOf(ORDERED_MILESTONES);

    public static final int AMOUNT_FOR_STREAK = 10;
    public static final Integer DEFAULT_STATUS_ID = 1; // e.g., PENDING
    public static final Integer DEFAULT_TYPE_ID = 1;   // e.g., Advertisement

    // Helper: status IDs
    public static final int STATUS_REQUESTED = 1;
    public static final int STATUS_FRIENDS = 2;
    public static final int STATUS_BLOCKED_BY_USER = 3;
    public static final int STATUS_UNFRIENDED = 4;
    public static final int STATUS_BLOCKED_BY_FRIEND = 5;
    public static final int STATUS_RECEIVED = 6;
    // ======================================================================
    // Increase this value (e.g., to 5, 7, 14) as your user base scales to millions
    // to give manual audit teams and fraud verification queues more time to clear.
    public static final int WORKOUT_COOLING_DAYS = 0; // TODO will need to find a good date for these
    // Define a reusable, thread-safe formatter
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
