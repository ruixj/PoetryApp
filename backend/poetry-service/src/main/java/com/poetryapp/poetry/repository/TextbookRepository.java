package com.poetryapp.poetry.repository;

import com.poetryapp.poetry.entity.TextbookSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TextbookRepository extends JpaRepository<TextbookSystem, Long> {
    List<TextbookSystem> findAllByOrderByOrderNumAsc();
}
