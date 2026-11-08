package com.mldtech.nilapp.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // 🛡️ Safely hides empty list keys on the JSON response
// TODO use this in error messages
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
        private Long targetId;
        private String name;
        private int stepsAttempted;
        private int coinsAttempted;
        private double allocationPercentage;
    }
}

