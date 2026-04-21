package com.poetryapp.game.repository;

import com.poetryapp.game.entity.UserRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;

@Mapper
public interface UserRefRepository {

    @Select("SELECT id, nickname, avatar_url, yuanbao_points FROM users WHERE id = #{userId}")
    Optional<UserRef> findById(Long userId);

    @Update("UPDATE users SET yuanbao_points = yuanbao_points + #{points} WHERE id = #{userId}")
    void addPoints(@Param("userId") Long userId, @Param("points") int points);
}
