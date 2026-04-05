package com.habitforge.dto;

import com.habitforge.entity.Habit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsDto {
    private List<CategoryPerformanceDto> categories;
    private String strongestCategory;
    private String weakestCategory;
    private String insight;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryPerformanceDto {
        private Habit.Category category;
        private Integer totalHabits;
        private Integer completedTasks;
        private Double completionRate;
        private Double percentage;
    }
}
