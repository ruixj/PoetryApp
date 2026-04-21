package com.poetryapp.game.repository;

import com.poetryapp.game.entity.GameSubmission;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GameSubmissionRepository {

    @Select("SELECT * FROM game_submissions "
          + "WHERE game_category_type=#{type} AND game_category_value=#{value} AND is_valid=1 "
          + "ORDER BY created_at DESC")
    List<GameSubmission> findByGameCategoryTypeAndGameCategoryValueAndIsValidTrueOrderByCreatedAtDesc(
            @Param("type") String type, @Param("value") String value);

    @Select("SELECT COUNT(1) > 0 FROM game_submissions "
          + "WHERE user_id=#{userId} AND poem_id=#{poemId} "
          + "AND game_category_type=#{type} AND game_category_value=#{value} AND is_valid=1")
    boolean existsByUserIdAndPoemIdAndGameCategoryTypeAndGameCategoryValueAndIsValidTrue(
            @Param("userId") Long userId, @Param("poemId") Long poemId,
            @Param("type") String type, @Param("value") String value);

    @Insert("INSERT INTO game_submissions(user_id, poem_id, game_category_type, game_category_value, "
          + "input_text, is_valid, points_earned, created_at) "
          + "VALUES(#{userId}, #{poemId}, #{gameCategoryType}, #{gameCategoryValue}, "
          + "#{inputText}, #{isValid}, #{pointsEarned}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(GameSubmission submission);
}
