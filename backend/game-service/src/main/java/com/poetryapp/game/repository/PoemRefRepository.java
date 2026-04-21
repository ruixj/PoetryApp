package com.poetryapp.game.repository;

import com.poetryapp.game.entity.PoemRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface PoemRefRepository {

    @Select("SELECT * FROM poems WHERE id = #{id}")
    Optional<PoemRef> findById(Long id);

    @Select("SELECT * FROM poems WHERE title = #{input} "
          + "OR content LIKE CONCAT('%', #{input}, '%') "
          + "OR #{input} LIKE CONCAT('%', title, '%') LIMIT 1")
    Optional<PoemRef> findByInput(@Param("input") String input);
}
