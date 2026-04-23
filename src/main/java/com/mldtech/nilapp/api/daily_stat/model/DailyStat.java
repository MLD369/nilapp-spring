package com.mldtech.nilapp.api.daily_stat.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "daily_stats",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "date"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_stat_id")
    private Long dailyStatId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "total_steps")
    private Integer totalSteps;

    private Integer coins;

    @Column(name = "distance_meters")
    private Integer distanceMeters;

    @Column(name = "device_source")
    private String deviceSource;

    @Column(name = "sync_timestamp")
    private LocalDateTime syncTimestamp;

    private Integer calories;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
