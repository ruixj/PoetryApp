package com.poetryapp.user.repository;

import com.poetryapp.user.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface UserRepository {

    @Select("SELECT * FROM users WHERE id = #{id}")
    Optional<User> findById(Long id);

    @Select("SELECT * FROM users WHERE phone = #{phone}")
    Optional<User> findByPhone(String phone);

    @Update("UPDATE users SET nickname=#{nickname}, avatar_url=#{avatarUrl}, "
          + "is_first_login=#{isFirstLogin}, textbook_id=#{textbookId}, grade_id=#{gradeId}, "
          + "updated_at=NOW() WHERE id=#{id}")
    void update(User user);
}
