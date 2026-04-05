package com.habitforge.service;

import com.habitforge.dto.HabitDto;
import com.habitforge.entity.Habit;
import com.habitforge.entity.User;
import com.habitforge.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitService {
    
    private final HabitRepository habitRepository;
    
    @Transactional
    public HabitDto createHabit(HabitDto habitDto, User user) {
        Habit habit = Habit.builder()
                .user(user)
                .name(habitDto.getName())
                .description(habitDto.getDescription())
                .category(habitDto.getCategory())
                .frequency(habitDto.getFrequency())
                .build();
        
        Habit savedHabit = habitRepository.save(habit);
        log.info("Created new habit: {} for user: {}", savedHabit.getName(), user.getEmail());
        
        return HabitDto.fromEntity(savedHabit);
    }
    
    @Transactional
    public HabitDto updateHabit(Long habitId, HabitDto habitDto, User user) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new RuntimeException("Habit not found or doesn't belong to user"));
        
        habit.setName(habitDto.getName());
        habit.setDescription(habitDto.getDescription());
        habit.setCategory(habitDto.getCategory());
        habit.setFrequency(habitDto.getFrequency());
        
        Habit updatedHabit = habitRepository.save(habit);
        log.info("Updated habit: {} for user: {}", updatedHabit.getName(), user.getEmail());
        
        return HabitDto.fromEntity(updatedHabit);
    }
    
    @Transactional
    public void deleteHabit(Long habitId, User user) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new RuntimeException("Habit not found or doesn't belong to user"));
        
        habitRepository.delete(habit);
        log.info("Deleted habit: {} for user: {}", habit.getName(), user.getEmail());
    }
    
    @Transactional(readOnly = true)
    public HabitDto getHabitById(Long habitId, User user) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new RuntimeException("Habit not found or doesn't belong to user"));
        
        return HabitDto.fromEntity(habit);
    }
    
    @Transactional(readOnly = true)
    public Page<HabitDto> getUserHabits(User user, Pageable pageable) {
        return habitRepository.findByUser(user, pageable)
                .map(HabitDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public List<HabitDto> getAllUserHabits(User user) {
        return habitRepository.findByUser(user).stream()
                .map(HabitDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<HabitDto> getHabitsByCategory(User user, Habit.Category category) {
        return habitRepository.findByUserAndCategory(user, category).stream()
                .map(HabitDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public long getTotalHabitsCount(User user) {
        return habitRepository.countByUser(user);
    }
}
