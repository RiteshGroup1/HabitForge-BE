package com.habitforge.controller;

import com.habitforge.dto.HabitLogDto;
import com.habitforge.entity.User;
import com.habitforge.service.HabitLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/habit-log")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class HabitLogController {
    
    private final HabitLogService habitLogService;
    
    @PostMapping
    public ResponseEntity<HabitLogDto> createOrUpdateHabitLog(
            @Valid @RequestBody HabitLogDto habitLogDto,
            @AuthenticationPrincipal User user) {
        
        log.info("Creating/updating habit log for habit: {} on date: {} for user: {}", 
                habitLogDto.getHabitId(), habitLogDto.getDate(), user.getEmail());
        
        HabitLogDto result = habitLogService.createOrUpdateHabitLog(habitLogDto, user);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/habit/{habitId}")
    public ResponseEntity<List<HabitLogDto>> getHabitLogsByHabit(
            @PathVariable Long habitId,
            @AuthenticationPrincipal User user) {
        
        List<HabitLogDto> logs = habitLogService.getHabitLogsByHabit(habitId, user);
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<HabitLogDto>> getHabitLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User user) {
        
        List<HabitLogDto> logs = habitLogService.getHabitLogsByDateRange(user, startDate, endDate);
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/date/{date}")
    public ResponseEntity<List<HabitLogDto>> getHabitLogsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user) {
        
        List<HabitLogDto> logs = habitLogService.getHabitLogsByDate(user, date);
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/today")
    public ResponseEntity<List<HabitLogDto>> getTodayHabitLogs(@AuthenticationPrincipal User user) {
        List<HabitLogDto> logs = habitLogService.getHabitLogsByDate(user, LocalDate.now());
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/stats/today")
    public ResponseEntity<String> getTodayStats(@AuthenticationPrincipal User user) {
        long completed = habitLogService.getCompletedHabitsCount(user, LocalDate.now());
        long total = habitLogService.getTotalHabitsCount(user);
        
        String stats = String.format("Today: %d/%d habits completed", completed, total);
        return ResponseEntity.ok(stats);
    }
}
