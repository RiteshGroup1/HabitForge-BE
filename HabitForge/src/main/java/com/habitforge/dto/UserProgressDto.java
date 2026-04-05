package com.habitforge.dto;

import com.habitforge.entity.UserProgress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressDto {
    private Long id;
    private Integer totalPoints;
    private Integer level;
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDateTime lastUpdated;
    private Integer totalCompletedTasks;
    private Double completionRatio;
    
    public static UserProgressDto fromEntity(UserProgress progress) {
        return UserProgressDto.builder()
                .id(progress.getId())
                .totalPoints(progress.getTotalPoints())
                .level(progress.getLevel())
                .currentStreak(progress.getCurrentStreak())
                .longestStreak(progress.getLongestStreak())
                .lastUpdated(progress.getLastUpdated())
                .totalCompletedTasks(progress.getTotalCompletedTasks())
                .completionRatio(progress.getCompletionRatio())
                .build();
    }
}
