package com.poetryapp.game.repository;

import com.poetryapp.game.entity.PoemCategoryRef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PoemCategoryRefRepository extends JpaRepository<PoemCategoryRef, Long> {
    List<PoemCategoryRef> findByPoemIdAndCategoryTypeAndCategoryValue(
            Long poemId, String type, String value);
}
