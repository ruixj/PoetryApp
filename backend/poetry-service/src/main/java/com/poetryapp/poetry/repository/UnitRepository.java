package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.Unit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UnitRepository {

    @Select("SELECT * FROM units WHERE grade_id = #{gradeId} ORDER BY order_num ASC")
    List<Unit> findByGradeIdOrderByOrderNumAsc(Long gradeId);
}
