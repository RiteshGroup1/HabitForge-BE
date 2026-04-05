package com.habitforge.repository;

import com.habitforge.entity.Habit;
import com.habitforge.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    
    Page<Habit> findByUser(User user, Pageable pageable);
    
    List<Habit> findByUser(User user);
    
    Optional<Habit> findByIdAndUser(Long id, User user);
    
    @Query("SELECT h FROM Habit h WHERE h.user = :user AND h.category = :category")
    List<Habit> findByUserAndCategory(@Param("user") User user, @Param("category") Habit.Category category);
    
    @Query("SELECT COUNT(h) FROM Habit h WHERE h.user = :user")
    Long countByUser(@Param("user") User user);
    
    @Query("SELECT h.category, COUNT(h) FROM Habit h WHERE h.user = :user GROUP BY h.category")
    List<Object[]> countHabitsByCategory(@Param("user") User user);
}
