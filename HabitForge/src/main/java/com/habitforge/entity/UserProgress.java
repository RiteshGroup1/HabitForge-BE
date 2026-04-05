package com.habitforge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;
    
    @Column(nullable = false)
    private Integer level;
    
    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak;
    
    @Column(name = "longest_streak", nullable = false)
    private Integer longestStreak;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @Column(name = "total_completed_tasks")
    private Integer totalCompletedTasks;
    
    @Column(name = "completion_ratio")
    private Double completionRatio;
}
