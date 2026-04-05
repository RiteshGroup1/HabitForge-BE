package com.habitforge.repository;

import com.habitforge.entity.Habit;
import com.habitforge.entity.HabitLog;
import com.habitforge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
    
    Optional<HabitLog> findByHabitAndDate(Habit habit, LocalDate date);
    
    List<HabitLog> findByHabit(Habit habit);
    
    List<HabitLog> findByHabit_UserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    List<HabitLog> findByHabit_UserAndDate(User user, LocalDate date);
    
    @Query("SELECT COUNT(hl) FROM HabitLog hl WHERE hl.habit.user = :user AND hl.date = :date AND hl.status = true")
    Long countCompletedHabitsByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(h) FROM Habit h WHERE h.user = :user")
    Long countTotalHabitsByUser(@Param("user") User user);
    
    @Query("SELECT hl FROM HabitLog hl WHERE hl.habit.user = :user AND hl.status = true ORDER BY hl.date DESC")
    List<HabitLog> findCompletedLogsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(hl) FROM HabitLog hl WHERE hl.habit.user = :user AND hl.status = true")
    Long countTotalCompletedTasksByUser(@Param("user") User user);
    
    @Query("SELECT hl.date, COUNT(hl) FROM HabitLog hl WHERE hl.habit.user = :user AND hl.status = true " +
           "GROUP BY hl.date ORDER BY hl.date DESC")
    List<Object[]> getDailyCompletionStats(@Param("user") User user);
    
    @Query("SELECT h.category, COUNT(hl) FROM HabitLog hl " +
           "JOIN hl.habit h WHERE h.user = :user AND hl.status = true " +
           "GROUP BY h.category")
    List<Object[]> getCategoryCompletionStats(@Param("user") User user);
}
