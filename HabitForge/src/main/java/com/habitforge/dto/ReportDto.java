package com.habitforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private Integer completedDays;
    private Double successRate;
    private List<DailyProgressDto> dailyProgress;
    private Map<String, Double> categoryPerformance;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyProgressDto {
        private LocalDate date;
        private Integer totalHabits;
        private Integer completedHabits;
        private Double successRate;
    }
}
