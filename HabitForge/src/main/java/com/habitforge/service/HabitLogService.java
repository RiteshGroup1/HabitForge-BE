package com.habitforge.service;

import com.habitforge.dto.HabitLogDto;
import com.habitforge.entity.Habit;
import com.habitforge.entity.HabitLog;
import com.habitforge.entity.User;
import com.habitforge.repository.HabitLogRepository;
import com.habitforge.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitLogService {
    
    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;
    private final UserProgressService userProgressService;
    
    @Transactional
    public HabitLogDto createOrUpdateHabitLog(HabitLogDto habitLogDto, User user) {
        Habit habit = habitRepository.findByIdAndUser(habitLogDto.getHabitId(), user)
                .orElseThrow(() -> new RuntimeException("Habit not found or doesn't belong to user"));
        
        HabitLog existingLog = habitLogRepository.findByHabitAndDate(habit, habitLogDto.getDate());
        
        if (existingLog != null) {
            existingLog.setStatus(habitLogDto.getStatus());
            HabitLog updatedLog = habitLogRepository.save(existingLog);
            log.info("Updated habit log for habit: {} on date: {}", habit.getName(), habitLogDto.getDate());
            
            // Update user progress
            userProgressService.updateUserProgress(user);
            
            return HabitLogDto.fromEntity(updatedLog);
        } else {
            HabitLog newLog = HabitLog.builder()
                    .habit(habit)
                    .date(habitLogDto.getDate())
                    .status(habitLogDto.getStatus())
                    .build();
            
            HabitLog savedLog = habitLogRepository.save(newLog);
            log.info("Created new habit log for habit: {} on date: {}", habit.getName(), habitLogDto.getDate());
            
            // Update user progress
            userProgressService.updateUserProgress(user);
            
            return HabitLogDto.fromEntity(savedLog);
        }
    }
    
    @Transactional(readOnly = true)
    public List<HabitLogDto> getHabitLogsByHabit(Long habitId, User user) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new RuntimeException("Habit not found or doesn't belong to user"));
        
        return habitLogRepository.findByHabit(habit).stream()
                .map(HabitLogDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<HabitLogDto> getHabitLogsByDateRange(User user, LocalDate startDate, LocalDate endDate) {
        return habitLogRepository.findByHabit_UserAndDateBetween(user, startDate, endDate).stream()
                .map(HabitLogDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<HabitLogDto> getHabitLogsByDate(User user, LocalDate date) {
        return habitLogRepository.findByHabit_UserAndDate(user, date).stream()
                .map(HabitLogDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public long getCompletedHabitsCount(User user, LocalDate date) {
        return habitLogRepository.countCompletedHabitsByUserAndDate(user, date);
    }
    
    @Transactional(readOnly = true)
    public long getTotalHabitsCount(User user) {
        return habitLogRepository.countTotalHabitsByUser(user);
    }
    
    @Transactional(readOnly = true)
    public List<HabitLog> getCompletedLogsByUser(User user) {
        return habitLogRepository.findCompletedLogsByUser(user);
    }
    
    @Transactional(readOnly = true)
    public long getTotalCompletedTasksByUser(User user) {
        return habitLogRepository.countTotalCompletedTasksByUser(user);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getDailyCompletionStats(User user) {
        return habitLogRepository.getDailyCompletionStats(user);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getCategoryCompletionStats(User user) {
        return habitLogRepository.getCategoryCompletionStats(user);
    }
}
