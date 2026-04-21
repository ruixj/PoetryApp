package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.PoemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PoemCategoryRepository extends JpaRepository<PoemCategory, Long> {
    List<PoemCategory> findByPoemId(Long poemId);

    /** 按分类类型查询所有不重复的值 */
    @org.springframework.data.jpa.repository.Query(
        "SELECT DISTINCT c.categoryValue FROM PoemCategory c WHERE c.categoryType = :type ORDER BY c.categoryValue")
    List<String> findDistinctValuesByType(String type);
}
