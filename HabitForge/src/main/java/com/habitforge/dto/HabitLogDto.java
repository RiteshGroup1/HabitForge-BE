package com.habitforge.dto;

import com.habitforge.entity.HabitLog;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitLogDto {
    private Long id;
    private Long habitId;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Status is required")
    private Boolean status;
    
    private LocalDateTime createdAt;
    
    public static HabitLogDto fromEntity(HabitLog habitLog) {
        return HabitLogDto.builder()
                .id(habitLog.getId())
                .habitId(habitLog.getHabit().getId())
                .date(habitLog.getDate())
                .status(habitLog.getStatus())
                .createdAt(habitLog.getCreatedAt())
                .build();
    }
}
