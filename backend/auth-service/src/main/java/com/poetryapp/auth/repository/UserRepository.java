package com.poetryapp.auth.repository;

import com.poetryapp.auth.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface UserRepository {

    @Select("SELECT * FROM users WHERE id = #{id}")
    Optional<User> findById(Long id);

    @Select("SELECT * FROM users WHERE phone = #{phone}")
    Optional<User> findByPhone(String phone);

    @Select("SELECT COUNT(1) > 0 FROM users WHERE phone = #{phone}")
    boolean existsByPhone(String phone);

    @Insert("INSERT INTO users(phone, password, nickname, role, is_first_login, yuanbao_points, total_study_minutes, created_at, updated_at) "
          + "VALUES(#{phone}, #{password}, #{nickname}, #{role}, #{isFirstLogin}, #{yuanbaoPoints}, #{totalStudyMinutes}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE users SET nickname=#{nickname}, avatar_url=#{avatarUrl}, yuanbao_points=#{yuanbaoPoints}, "
          + "total_study_minutes=#{totalStudyMinutes}, is_first_login=#{isFirstLogin}, "
          + "textbook_id=#{textbookId}, grade_id=#{gradeId}, updated_at=NOW() WHERE id=#{id}")
    void update(User user);
}
