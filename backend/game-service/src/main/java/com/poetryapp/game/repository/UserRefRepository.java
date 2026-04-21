package com.poetryapp.game.repository;

import com.poetryapp.game.entity.UserRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRefRepository extends JpaRepository<UserRef, Long> {
    @Modifying
    @Query("UPDATE UserRef u SET u.yuanbaoPoints = u.yuanbaoPoints + :points WHERE u.id = :userId")
    void addPoints(Long userId, int points);
}
