package com.poetryapp.shop.repository;

import com.poetryapp.shop.entity.UserShopRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserShopRefRepository extends JpaRepository<UserShopRef, Long> {
    @Modifying
    @Query("UPDATE UserShopRef u SET u.yuanbaoPoints = u.yuanbaoPoints - :points WHERE u.id = :userId")
    int deductPoints(Long userId, int points);
}
