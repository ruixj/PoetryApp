package com.poetryapp.game.repository;

import com.poetryapp.game.entity.PoemCategoryRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PoemCategoryRefRepository {

    @Select("SELECT * FROM poem_categories "
          + "WHERE poem_id=#{poemId} AND category_type=#{type} AND category_value=#{value}")
    List<PoemCategoryRef> findByPoemIdAndCategoryTypeAndCategoryValue(
            @Param("poemId") Long poemId, @Param("type") String type, @Param("value") String value);
}
