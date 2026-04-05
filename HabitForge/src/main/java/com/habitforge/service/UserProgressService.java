package com.habitforge.service;

import com.habitforge.entity.User;
import com.habitforge.entity.UserProgress;
import com.habitforge.repository.UserProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProgressService {
    
    private final UserProgressRepository userProgressRepository;
    private final HabitLogService habitLogService;
    private final HabitService habitService;
    
    @Transactional
    public UserProgress updateOrCreateUserProgress(User user) {
        UserProgress progress = userProgressRepository.findByUser(user);
        
        if (progress == null) {
            progress = createInitialProgress(user);
        }
        
        updateProgressMetrics(user, progress);
        UserProgress savedProgress = userProgressRepository.save(progress);
        
        log.info("Updated progress for user: {} - Level: {}, Points: {}, Streak: {}", 
                user.getEmail(), savedProgress.getLevel(), savedProgress.getTotalPoints(), savedProgress.getCurrentStreak());
        
        return savedProgress;
    }
    
    @Transactional
    public void updateUserProgress(User user) {
        updateOrCreateUserProgress(user);
    }
    
    private UserProgress createInitialProgress(User user) {
        UserProgress progress = UserProgress.builder()
                .user(user)
                .totalPoints(0)
                .level(1)
                .currentStreak(0)
                .longestStreak(0)
                .totalCompletedTasks(0)
                .completionRatio(0.0)
                .lastUpdated(LocalDateTime.now())
                .build();
        
        return userProgressRepository.save(progress);
    }
    
    private void updateProgressMetrics(User user, UserProgress progress) {
        // Get current metrics
        long totalCompletedTasks = habitLogService.getTotalCompletedTasksByUser(user);
        long totalHabits = habitService.getTotalHabitsCount(user);
        
        // Calculate completion ratio for today
        long completedToday = habitLogService.getCompletedHabitsCount(user, LocalDate.now());
        double completionRatio = totalHabits > 0 ? (double) completedToday / totalHabits : 0.0;
        
        // Calculate current streak
        int currentStreak = calculateCurrentStreak(user);
        
        // Calculate score using weighted formula
        double score = calculateScore(completionRatio, totalCompletedTasks, currentStreak);
        
        // Calculate level
        int level = calculateLevel(score);
        
        // Update progress
        progress.setTotalPoints((int) score);
        progress.setLevel(level);
        progress.setCurrentStreak(currentStreak);
        progress.setLongestStreak(Math.max(progress.getLongestStreak(), currentStreak));
        progress.setTotalCompletedTasks((int) totalCompletedTasks);
        progress.setCompletionRatio(completionRatio);
        progress.setLastUpdated(LocalDateTime.now());
    }
    
    private double calculateScore(double completionRatio, long totalCompletedTasks, int currentStreak) {
        // Weighted scoring formula
        double score = (completionRatio * 50) + 
                      (Math.log(totalCompletedTasks + 1) * 30) + 
                      (currentStreak * 2);
        
        return Math.round(score);
    }
    
    private int calculateLevel(double score) {
        // Dynamic level calculation
        return (int) (score / 100) + 1;
    }
    
    private int calculateCurrentStreak(User user) {
        List<Object[]> dailyStats = habitLogService.getDailyCompletionStats(user);
        
        if (dailyStats.isEmpty()) {
            return 0;
        }
        
        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate checkDate = today.minusDays(1); // Start checking from yesterday
        
        // Check if there's any completion today
        boolean completedToday = habitLogService.getCompletedHabitsCount(user, today) > 0;
        
        if (!completedToday) {
            // If not completed today, check if yesterday was completed
            checkDate = today.minusDays(1);
        } else {
            streak = 1; // Today is completed
        }
        
        // Count consecutive days backwards
        for (Object[] stat : dailyStats) {
            LocalDate statDate = (LocalDate) stat[0];
            Long completedCount = (Long) stat[1];
            
            if (statDate.isEqual(checkDate) && completedCount > 0) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else if (statDate.isBefore(checkDate)) {
                break;
            }
        }
        
        return streak;
    }
    
    @Transactional(readOnly = true)
    public UserProgress getUserProgress(User user) {
        UserProgress progress = userProgressRepository.findByUser(user);
        if (progress == null) {
            return createInitialProgress(user);
        }
        return progress;
    }
}
