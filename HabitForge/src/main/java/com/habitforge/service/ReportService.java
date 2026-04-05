package com.habitforge.service;

import com.habitforge.dto.ReportDto;
import com.habitforge.entity.Habit;
import com.habitforge.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    
    private final HabitLogService habitLogService;
    private final HabitService habitService;
    
    @Transactional(readOnly = true)
    public ReportDto generateWeeklyReport(User user) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(1);
        
        return generateReport(user, startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public ReportDto generateMonthlyReport(User user) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);
        
        return generateReport(user, startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public ReportDto generateCustomReport(User user, LocalDate startDate, LocalDate endDate) {
        return generateReport(user, startDate, endDate);
    }
    
    private ReportDto generateReport(User user, LocalDate startDate, LocalDate endDate) {
        List<ReportDto.DailyProgressDto> dailyProgress = new ArrayList<>();
        int totalDays = 0;
        int completedDays = 0;
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            long completedHabits = habitLogService.getCompletedHabitsCount(user, currentDate);
            long totalHabits = habitLogService.getTotalHabitsCount(user);
            
            double dailySuccessRate = totalHabits > 0 ? (double) completedHabits / totalHabits * 100 : 0.0;
            
            if (totalHabits > 0) {
                totalDays++;
                if (completedHabits > 0) {
                    completedDays++;
                }
            }
            
            dailyProgress.add(ReportDto.DailyProgressDto.builder()
                    .date(currentDate)
                    .totalHabits((int) totalHabits)
                    .completedHabits((int) completedHabits)
                    .successRate(dailySuccessRate)
                    .build());
            
            currentDate = currentDate.plusDays(1);
        }
        
        double successRate = totalDays > 0 ? (double) completedDays / totalDays * 100 : 0.0;
        
        // Generate category performance
        Map<String, Double> categoryPerformance = generateCategoryPerformance(user);
        
        return ReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalDays(totalDays)
                .completedDays(completedDays)
                .successRate(successRate)
                .dailyProgress(dailyProgress)
                .categoryPerformance(categoryPerformance)
                .build();
    }
    
    private Map<String, Double> generateCategoryPerformance(User user) {
        Map<String, Double> categoryPerformance = new HashMap<>();
        
        // Get all habits by category and calculate completion rates
        for (Habit.Category category : Habit.Category.values()) {
            List<Habit> habits = habitService.getHabitsByCategory(user, category).stream()
                    .map(dto -> {
                        Habit habit = new Habit();
                        habit.setId(dto.getId());
                        habit.setCategory(dto.getCategory());
                        return habit;
                    })
                    .toList();
            
            if (!habits.isEmpty()) {
                long totalCompleted = 0;
                long totalPossible = 0;
                
                for (Habit habit : habits) {
                    // This is a simplified calculation - in production you'd want more sophisticated logic
                    totalPossible += habitLogService.getHabitLogsByHabit(habit.getId(), user).size();
                    totalCompleted += habitLogService.getHabitLogsByHabit(habit.getId(), user).stream()
                            .filter(log -> Boolean.TRUE.equals(log.getStatus()))
                            .count();
                }
                
                double completionRate = totalPossible > 0 ? (double) totalCompleted / totalPossible * 100 : 0.0;
                categoryPerformance.put(category.name(), completionRate);
            }
        }
        
        return categoryPerformance;
    }
}
