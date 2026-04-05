package com.habitforge.controller;

import com.habitforge.dto.CategoryStatsDto;
import com.habitforge.dto.UserProgressDto;
import com.habitforge.entity.User;
import com.habitforge.service.CategoryStatsService;
import com.habitforge.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProgressController {
    
    private final UserProgressService userProgressService;
    private final CategoryStatsService categoryStatsService;
    
    @GetMapping
    public ResponseEntity<UserProgressDto> getUserProgress(@AuthenticationPrincipal User user) {
        log.info("Getting progress for user: {}", user.getEmail());
        
        // Update progress before returning
        var progress = userProgressService.updateOrCreateUserProgress(user);
        UserProgressDto progressDto = UserProgressDto.fromEntity(progress);
        
        return ResponseEntity.ok(progressDto);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<CategoryStatsDto> getCategoryStats(@AuthenticationPrincipal User user) {
        log.info("Getting category stats for user: {}", user.getEmail());
        
        CategoryStatsDto categoryStats = categoryStatsService.getCategoryStats(user);
        return ResponseEntity.ok(categoryStats);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<UserProgressDto> refreshProgress(@AuthenticationPrincipal User user) {
        log.info("Refreshing progress for user: {}", user.getEmail());
        
        var progress = userProgressService.updateOrCreateUserProgress(user);
        UserProgressDto progressDto = UserProgressDto.fromEntity(progress);
        
        return ResponseEntity.ok(progressDto);
    }
}
