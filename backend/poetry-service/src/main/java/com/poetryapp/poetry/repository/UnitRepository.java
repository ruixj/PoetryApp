package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByGradeIdOrderByOrderNumAsc(Long gradeId);
}
