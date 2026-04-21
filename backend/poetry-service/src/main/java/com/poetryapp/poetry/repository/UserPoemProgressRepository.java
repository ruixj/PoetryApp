package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.UserPoemProgress;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserPoemProgressRepository {

    @Select("SELECT * FROM user_poem_progress WHERE user_id=#{userId} AND poem_id=#{poemId}")
    Optional<UserPoemProgress> findByUserIdAndPoemId(Long userId, Long poemId);

    @Select("SELECT * FROM user_poem_progress WHERE user_id=#{userId} AND is_completed=1 ORDER BY completed_at DESC")
    List<UserPoemProgress> findByUserIdAndIsCompletedTrueOrderByCompletedAtDesc(Long userId);

    @Select("SELECT COUNT(*) FROM user_poem_progress WHERE user_id=#{userId} AND is_completed=1")
    int countCompletedByUserId(Long userId);

    @Insert("INSERT INTO user_poem_progress(user_id, poem_id, current_stage, is_completed) "
          + "VALUES(#{userId}, #{poemId}, #{currentStage}, #{isCompleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserPoemProgress progress);

    @Update("UPDATE user_poem_progress SET current_stage=#{currentStage}, is_completed=#{isCompleted}, "
          + "completed_at=#{completedAt}, recording_url=#{recordingUrl} WHERE id=#{id}")
    void update(UserPoemProgress progress);
}
