package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.UserPoemProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserPoemProgressRepository extends JpaRepository<UserPoemProgress, Long> {
    Optional<UserPoemProgress> findByUserIdAndPoemId(Long userId, Long poemId);
    List<UserPoemProgress> findByUserIdAndIsCompletedTrueOrderByCompletedAtDesc(Long userId);

    @Query("SELECT COUNT(p) FROM UserPoemProgress p WHERE p.userId = :userId AND p.isCompleted = true")
    int countCompletedByUserId(@Param("userId") Long userId);
}
