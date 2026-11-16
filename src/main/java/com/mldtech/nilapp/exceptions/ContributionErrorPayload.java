package com.mldtech.nilapp.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // 🛡️ Safely hides empty list keys on the JSON response

public class ContributionErrorPayload {

    private boolean allowedToProcess;
    private String statusText;
    private String systemMessage;

    // Detailed breakdown contexts populated only when closed goals are caught
    private List<ClosedTargetDetail> closedEntities;
    private List<ClosedTargetDetail> closedGroups;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClosedTargetDetail {
        private Long targetId;         // Entity ID or Group ID
        private String targetName;     // Entity Name or Group Name
        private Long goalInstanceId;   // Exact GoalInstance ID that closed
        private String name;           // Exact Goal Name (e.g., "Kids' Active Zone")
        private int stepsAttempted;
        private int coinsAttempted;
        private double allocationPercentage;
    }
}

