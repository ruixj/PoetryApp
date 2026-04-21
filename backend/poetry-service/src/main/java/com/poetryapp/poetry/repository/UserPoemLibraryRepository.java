package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.UserPoemLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserPoemLibraryRepository extends JpaRepository<UserPoemLibrary, Long> {
    List<UserPoemLibrary> findByUserIdOrderByAddedAtDesc(Long userId);
    boolean existsByUserIdAndPoemId(Long userId, Long poemId);

    @Query("SELECT l.poemId FROM UserPoemLibrary l WHERE l.userId = :userId")
    List<Long> findPoemIdsByUserId(@Param("userId") Long userId);
}
