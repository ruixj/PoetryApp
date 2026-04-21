package com.poetryapp.auth.repository;

import com.poetryapp.auth.entity.LoginRecord;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface LoginRecordRepository {

    @Select("SELECT * FROM login_records WHERE user_id=#{userId} AND logout_time IS NULL "
          + "ORDER BY login_time DESC LIMIT 1")
    Optional<LoginRecord> findTopByUserIdAndLogoutTimeIsNullOrderByLoginTimeDesc(Long userId);

    @Insert("INSERT INTO login_records(user_id, login_time, duration_minutes) "
          + "VALUES(#{userId}, #{loginTime}, #{durationMinutes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(LoginRecord record);

    @Update("UPDATE login_records SET logout_time=#{logoutTime}, duration_minutes=#{durationMinutes} WHERE id=#{id}")
    void update(LoginRecord record);
}
