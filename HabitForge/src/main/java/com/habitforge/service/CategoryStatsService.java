package com.habitforge.service;

import com.habitforge.dto.CategoryStatsDto;
import com.habitforge.dto.HabitDto;
import com.habitforge.entity.Habit;
import com.habitforge.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryStatsService {
    
    private final HabitService habitService;
    private final HabitLogService habitLogService;
    
    @Transactional(readOnly = true)
    public CategoryStatsDto getCategoryStats(User user) {
        List<CategoryStatsDto.CategoryPerformanceDto> categoryPerformances = new ArrayList<>();
        
        for (Habit.Category category : Habit.Category.values()) {
            List<HabitDto> habits = habitService.getHabitsByCategory(user, category);
            
            if (!habits.isEmpty()) {
                int totalHabits = habits.size();
                int completedTasks = 0;
                
                for (HabitDto habit : habits) {
                    completedTasks += habitLogService.getHabitLogsByHabit(habit.getId(), user).stream()
                            .filter(log -> Boolean.TRUE.equals(log.getStatus()))
                            .count();
                }
                
                // Calculate completion rate (simplified - you might want to make this more sophisticated)
                double completionRate = totalHabits > 0 ? (double) completedTasks / (totalHabits * 30) * 100 : 0.0;
                completionRate = Math.min(completionRate, 100.0); // Cap at 100%
                
                categoryPerformances.add(CategoryStatsDto.CategoryPerformanceDto.builder()
                        .category(category)
                        .totalHabits(totalHabits)
                        .completedTasks(completedTasks)
                        .completionRate(completionRate)
                        .percentage(completionRate)
                        .build());
            }
        }
        
        // Sort by completion rate
        categoryPerformances.sort(Comparator.comparing(CategoryStatsDto.CategoryPerformanceDto::getCompletionRate).reversed());
        
        // Generate insights
        String strongestCategory = categoryPerformances.isEmpty() ? "None" : categoryPerformances.get(0).getCategory().name();
        String weakestCategory = categoryPerformances.isEmpty() ? "None" : categoryPerformances.get(categoryPerformances.size() - 1).getCategory().name();
        
        String insight = generateInsight(categoryPerformances);
        
        return CategoryStatsDto.builder()
                .categories(categoryPerformances)
                .strongestCategory(strongestCategory)
                .weakestCategory(weakestCategory)
                .insight(insight)
                .build();
    }
    
    private String generateInsight(List<CategoryStatsDto.CategoryPerformanceDto> performances) {
        if (performances.isEmpty()) {
            return "Start tracking habits to see your category performance!";
        }
        
        CategoryStatsDto.CategoryPerformanceDto strongest = performances.get(0);
        CategoryStatsDto.CategoryPerformanceDto weakest = performances.get(performances.size() - 1);
        
        if (strongest.getCategory().equals(weakest.getCategory())) {
            return String.format("You're consistent in %s with %.1f%% completion rate. Keep it up!", 
                    strongest.getCategory().name(), strongest.getCompletionRate());
        }
        
        return String.format("You are strong in %s (%.1f%%) but could improve in %s (%.1f%%)", 
                strongest.getCategory().name(), strongest.getCompletionRate(),
                weakest.getCategory().name(), weakest.getCompletionRate());
    }
}
