package com.poetryapp.admin.mapper;

import com.poetryapp.admin.dto.UserSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserAdminMapper {

    @Select("SELECT id, phone, nickname, avatar_url, yuanbao_points, total_study_minutes, role, "
          + "DATE_FORMAT(created_at, '%Y-%m-%d %H:%i') AS created_at "
          + "FROM users ORDER BY id DESC LIMIT #{size} OFFSET #{offset}")
    List<UserSummary> findAll(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM users")
    long count();
}
