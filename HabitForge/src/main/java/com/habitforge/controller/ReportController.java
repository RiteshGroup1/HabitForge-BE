package com.habitforge.controller;

import com.habitforge.dto.ReportDto;
import com.habitforge.entity.User;
import com.habitforge.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportController {
    
    private final ReportService reportService;
    
    @GetMapping("/weekly")
    public ResponseEntity<ReportDto> getWeeklyReport(@AuthenticationPrincipal User user) {
        log.info("Generating weekly report for user: {}", user.getEmail());
        ReportDto report = reportService.generateWeeklyReport(user);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/monthly")
    public ResponseEntity<ReportDto> getMonthlyReport(@AuthenticationPrincipal User user) {
        log.info("Generating monthly report for user: {}", user.getEmail());
        ReportDto report = reportService.generateMonthlyReport(user);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/custom")
    public ResponseEntity<ReportDto> getCustomReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User user) {
        
        log.info("Generating custom report for user: {} from {} to {}", user.getEmail(), startDate, endDate);
        ReportDto report = reportService.generateCustomReport(user, startDate, endDate);
        return ResponseEntity.ok(report);
    }
}
