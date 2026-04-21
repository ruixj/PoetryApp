package com.poetryapp.admin.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface TextbookAdminMapper {

    @Insert("INSERT INTO textbook_systems(name, description, order_num, created_at) "
          + "VALUES(#{name}, #{description}, #{orderNum}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertTextbook(@Param("name") String name,
                        @Param("description") String description,
                        @Param("orderNum") int orderNum);

    @Insert("INSERT INTO grades(name, textbook_id, order_num) "
          + "VALUES(#{name}, #{textbookId}, #{orderNum})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertGrade(@Param("name") String name,
                     @Param("textbookId") Long textbookId,
                     @Param("orderNum") int orderNum);

    @Insert("INSERT INTO units(name, grade_id, order_num) VALUES(#{name}, #{gradeId}, #{orderNum})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUnit(@Param("name") String name,
                    @Param("gradeId") Long gradeId,
                    @Param("orderNum") int orderNum);

    @Insert("INSERT INTO unit_poems(unit_id, poem_id, order_num) VALUES(#{unitId}, #{poemId}, #{orderNum})")
    void linkPoemToUnit(@Param("unitId") Long unitId,
                        @Param("poemId") Long poemId,
                        @Param("orderNum") int orderNum);

    @Insert("INSERT INTO poem_categories(poem_id, category_type, category_value) "
          + "VALUES(#{poemId}, #{categoryType}, #{categoryValue})")
    void insertPoemCategory(@Param("poemId") Long poemId,
                            @Param("categoryType") String categoryType,
                            @Param("categoryValue") String categoryValue);
}
