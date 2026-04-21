package com.poetryapp.admin.mapper;

import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface PoemAdminMapper {

    @Insert("INSERT INTO poems(title, dynasty, author, content, pinyin, translation, background, "
          + "author_intro, mindmap_data, difficulty_words, created_at, updated_at) "
          + "VALUES(#{title}, #{dynasty}, #{author}, #{content}, #{pinyin}, #{translation}, #{background}, "
          + "#{authorIntro}, #{mindmapData}, #{difficultyWords}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(com.poetryapp.admin.entity.PoemAdmin poem);

    @Update("UPDATE poems SET title=#{title}, dynasty=#{dynasty}, author=#{author}, content=#{content}, "
          + "pinyin=#{pinyin}, translation=#{translation}, background=#{background}, "
          + "author_intro=#{authorIntro}, mindmap_data=#{mindmapData}, difficulty_words=#{difficultyWords}, "
          + "updated_at=NOW() WHERE id=#{id}")
    void update(com.poetryapp.admin.entity.PoemAdmin poem);

    @Select("SELECT * FROM poems WHERE id = #{id}")
    Optional<com.poetryapp.admin.entity.PoemAdmin> findById(Long id);

    @Select("SELECT * FROM poems ORDER BY id DESC LIMIT #{size} OFFSET #{offset}")
    java.util.List<com.poetryapp.admin.entity.PoemAdmin> findAll(@Param("offset") int offset, @Param("size") int size);

    @Delete("DELETE FROM poems WHERE id = #{id}")
    void deleteById(Long id);

    @Update("UPDATE poems SET audio_url=#{audioUrl}, updated_at=NOW() WHERE id=#{id}")
    void updateAudioUrl(@Param("id") Long id, @Param("audioUrl") String audioUrl);

    @Update("UPDATE poems SET animation_url=#{animationUrl}, updated_at=NOW() WHERE id=#{id}")
    void updateAnimationUrl(@Param("id") Long id, @Param("animationUrl") String animationUrl);
}
