package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.Poem;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PoemRepository {

    @Select("SELECT * FROM poems WHERE id = #{id}")
    Optional<Poem> findById(Long id);

    @Select("SELECT COUNT(1) > 0 FROM poems WHERE id = #{id}")
    boolean existsById(Long id);

    @Select("SELECT p.* FROM poems p "
          + "JOIN unit_poems up ON up.poem_id = p.id "
          + "WHERE up.unit_id = #{unitId} ORDER BY up.order_num ASC")
    List<Poem> findByUnitId(Long unitId);

    @Select("SELECT p.* FROM poems p "
          + "JOIN poem_categories c ON c.poem_id = p.id "
          + "WHERE c.category_type = #{type} AND c.category_value = #{value}")
    List<Poem> findByCategoryTypeAndValue(@Param("type") String type, @Param("value") String value);

    @Insert("INSERT INTO poems(title, dynasty, author, content, translation, background, author_intro, "
          + "audio_url, animation_url, created_at, updated_at) "
          + "VALUES(#{title}, #{dynasty}, #{author}, #{content}, #{translation}, #{background}, "
          + "#{authorIntro}, #{audioUrl}, #{animationUrl}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Poem poem);

    @Update("UPDATE poems SET title=#{title}, dynasty=#{dynasty}, author=#{author}, content=#{content}, "
          + "translation=#{translation}, background=#{background}, author_intro=#{authorIntro}, "
          + "audio_url=#{audioUrl}, animation_url=#{animationUrl}, updated_at=NOW() WHERE id=#{id}")
    void update(Poem poem);

    @Delete("DELETE FROM poems WHERE id = #{id}")
    void deleteById(Long id);
}
