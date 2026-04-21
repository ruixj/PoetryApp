package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GradeRepository {

    @Select("SELECT * FROM grades WHERE textbook_id = #{textbookId} ORDER BY order_num ASC")
    List<Grade> findByTextbookIdOrderByOrderNumAsc(Long textbookId);
}
