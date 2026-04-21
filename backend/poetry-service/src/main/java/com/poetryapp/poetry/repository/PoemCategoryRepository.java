package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.PoemCategory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PoemCategoryRepository {

    @Select("SELECT * FROM poem_categories WHERE poem_id = #{poemId}")
    List<PoemCategory> findByPoemId(Long poemId);

    @Select("SELECT DISTINCT category_value FROM poem_categories WHERE category_type = #{type} ORDER BY category_value")
    List<String> findDistinctValuesByType(String type);

    @Insert("INSERT INTO poem_categories(poem_id, category_type, category_value) "
          + "VALUES(#{poemId}, #{categoryType}, #{categoryValue})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PoemCategory category);
}
