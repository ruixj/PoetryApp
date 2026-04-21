package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.Poem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PoemRepository extends JpaRepository<Poem, Long> {

    /** 查询某单元下的所有古诗（按排序） */
    @Query("""
        SELECT p FROM Poem p
        JOIN UnitPoem up ON up.poemId = p.id
        WHERE up.unitId = :unitId
        ORDER BY up.orderNum ASC
    """)
    List<Poem> findByUnitId(@Param("unitId") Long unitId);

    /** 按分类查询 */
    @Query("""
        SELECT p FROM Poem p
        JOIN PoemCategory c ON c.poemId = p.id
        WHERE c.categoryType = :type AND c.categoryValue = :value
    """)
    List<Poem> findByCategoryTypeAndValue(@Param("type") String type, @Param("value") String value);
}
