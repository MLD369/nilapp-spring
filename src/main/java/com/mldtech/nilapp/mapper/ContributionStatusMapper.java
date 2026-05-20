package com.mldtech.nilapp.mapper;

public class ContributionStatusMapper {
    public Long mapStatusToId(String status) {
        return switch (status.toUpperCase()) {
            case "PENDING" -> 1L;
            case "VERIFIED" -> 2L;
            case "PAID" -> 3L;
            case "REJECTED" -> 4L;
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        };
    }
}
