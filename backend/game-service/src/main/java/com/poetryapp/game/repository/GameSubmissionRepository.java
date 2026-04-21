package com.poetryapp.game.repository;

import com.poetryapp.game.entity.GameSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameSubmissionRepository extends JpaRepository<GameSubmission, Long> {
    List<GameSubmission> findByGameCategoryTypeAndGameCategoryValueAndIsValidTrueOrderByCreatedAtDesc(
            String type, String value);

    Page<GameSubmission> findByGameCategoryTypeAndGameCategoryValueAndIsValidTrue(
            String type, String value, Pageable pageable);

    boolean existsByUserIdAndPoemIdAndGameCategoryTypeAndGameCategoryValueAndIsValidTrue(
            Long userId, Long poemId, String type, String value);
}
