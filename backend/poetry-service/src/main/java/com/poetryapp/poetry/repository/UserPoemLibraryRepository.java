package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.UserPoemLibrary;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserPoemLibraryRepository {

    @Select("SELECT * FROM user_poem_library WHERE user_id = #{userId} ORDER BY added_at DESC")
    List<UserPoemLibrary> findByUserIdOrderByAddedAtDesc(Long userId);

    @Select("SELECT COUNT(1) > 0 FROM user_poem_library WHERE user_id = #{userId} AND poem_id = #{poemId}")
    boolean existsByUserIdAndPoemId(Long userId, Long poemId);

    @Select("SELECT poem_id FROM user_poem_library WHERE user_id = #{userId}")
    List<Long> findPoemIdsByUserId(Long userId);

    @Insert("INSERT INTO user_poem_library(user_id, poem_id, added_at) VALUES(#{userId}, #{poemId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserPoemLibrary lib);
}
