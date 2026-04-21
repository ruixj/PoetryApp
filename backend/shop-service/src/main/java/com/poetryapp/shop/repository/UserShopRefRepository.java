package com.poetryapp.shop.repository;

import com.poetryapp.shop.entity.UserShopRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;

@Mapper
public interface UserShopRefRepository {

    @Select("SELECT id, yuanbao_points FROM users WHERE id = #{id}")
    Optional<UserShopRef> findById(Long id);

    @Update("UPDATE users SET yuanbao_points = yuanbao_points - #{points} WHERE id = #{userId}")
    int deductPoints(@Param("userId") Long userId, @Param("points") int points);
}
