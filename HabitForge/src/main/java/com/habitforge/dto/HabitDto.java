package com.habitforge.dto;

import com.habitforge.entity.Habit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitDto {
    private Long id;
    
    @NotBlank(message = "Habit name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Category is required")
    private Habit.Category category;
    
    @NotNull(message = "Frequency is required")
    private Habit.Frequency frequency;
    
    private LocalDateTime createdAt;
    
    public static HabitDto fromEntity(Habit habit) {
        return HabitDto.builder()
                .id(habit.getId())
                .name(habit.getName())
                .description(habit.getDescription())
                .category(habit.getCategory())
                .frequency(habit.getFrequency())
                .createdAt(habit.getCreatedAt())
                .build();
    }
}
