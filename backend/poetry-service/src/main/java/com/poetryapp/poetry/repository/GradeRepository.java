package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByTextbookIdOrderByOrderNumAsc(Long textbookId);
}
