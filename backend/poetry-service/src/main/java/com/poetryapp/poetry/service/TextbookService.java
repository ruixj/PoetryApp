package com.poetryapp.poetry.service;

import com.poetryapp.poetry.entity.*;
import com.poetryapp.poetry.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TextbookService {

    private final TextbookRepository textbookRepo;
    private final GradeRepository gradeRepo;
    private final UnitRepository unitRepo;

    public List<TextbookSystem> listTextbooks() {
        return textbookRepo.findAllByOrderByOrderNumAsc();
    }

    public List<Grade> listGrades(Long textbookId) {
        return gradeRepo.findByTextbookIdOrderByOrderNumAsc(textbookId);
    }

    public List<Unit> listUnits(Long gradeId) {
        return unitRepo.findByGradeIdOrderByOrderNumAsc(gradeId);
    }
}
