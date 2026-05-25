package com.mldtech.nilapp.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;

public class PeriodKey {
    public String getPeriodKey(LocalDateTime date, String periodType) {
        LocalDate d = date.toLocalDate();

        return switch (periodType.toUpperCase()) {
            case "DAILY" -> d.toString(); // 2026-05-24
            case "WEEKLY" -> d.getYear() + "-W" + d.get(WeekFields.ISO.weekOfWeekBasedYear());
            case "MONTHLY" -> d.getYear() + "-" + String.format("%02d", d.getMonthValue());
            case "YEARLY" -> String.valueOf(d.getYear());
            default -> d.toString();
        };
    }

}
