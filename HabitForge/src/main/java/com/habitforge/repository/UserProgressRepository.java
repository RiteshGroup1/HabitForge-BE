package com.habitforge.repository;

import com.habitforge.entity.User;
import com.habitforge.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    
    Optional<UserProgress> findByUser(User user);
    
    void deleteByUser(User user);
}
