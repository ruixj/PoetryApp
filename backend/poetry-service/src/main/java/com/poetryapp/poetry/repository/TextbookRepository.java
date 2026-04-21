package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.TextbookSystem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TextbookRepository {

    @Select("SELECT * FROM textbook_systems ORDER BY order_num ASC")
    List<TextbookSystem> findAllByOrderByOrderNumAsc();
}
