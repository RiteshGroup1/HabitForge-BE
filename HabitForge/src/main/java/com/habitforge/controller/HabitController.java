package com.habitforge.controller;

import com.habitforge.dto.HabitDto;
import com.habitforge.entity.Habit;
import com.habitforge.entity.User;
import com.habitforge.service.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habits")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class HabitController {
    
    private final HabitService habitService;
    
    @PostMapping
    public ResponseEntity<HabitDto> createHabit(
            @Valid @RequestBody HabitDto habitDto,
            @AuthenticationPrincipal User user) {
        
        log.info("Creating habit: {} for user: {}", habitDto.getName(), user.getEmail());
        HabitDto createdHabit = habitService.createHabit(habitDto, user);
        return ResponseEntity.ok(createdHabit);
    }
    
    @GetMapping
    public ResponseEntity<Page<HabitDto>> getUserHabits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal User user) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<HabitDto> habits = habitService.getUserHabits(user, pageable);
        return ResponseEntity.ok(habits);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<HabitDto>> getAllUserHabits(@AuthenticationPrincipal User user) {
        List<HabitDto> habits = habitService.getAllUserHabits(user);
        return ResponseEntity.ok(habits);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<HabitDto> getHabitById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        HabitDto habit = habitService.getHabitById(id, user);
        return ResponseEntity.ok(habit);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HabitDto> updateHabit(
            @PathVariable Long id,
            @Valid @RequestBody HabitDto habitDto,
            @AuthenticationPrincipal User user) {
        
        log.info("Updating habit: {} for user: {}", id, user.getEmail());
        HabitDto updatedHabit = habitService.updateHabit(id, habitDto, user);
        return ResponseEntity.ok(updatedHabit);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        log.info("Deleting habit: {} for user: {}", id, user.getEmail());
        habitService.deleteHabit(id, user);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<HabitDto>> getHabitsByCategory(
            @PathVariable Habit.Category category,
            @AuthenticationPrincipal User user) {
        
        List<HabitDto> habits = habitService.getHabitsByCategory(user, category);
        return ResponseEntity.ok(habits);
    }
}
