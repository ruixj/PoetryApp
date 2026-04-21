package com.poetryapp.poetry.controller;

import com.poetryapp.common.response.ApiResponse;
import com.poetryapp.poetry.entity.Grade;
import com.poetryapp.poetry.entity.TextbookSystem;
import com.poetryapp.poetry.entity.Unit;
import com.poetryapp.poetry.service.TextbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/poetry/textbooks")
@RequiredArgsConstructor
public class TextbookController {

    private final TextbookService textbookService;

    @GetMapping
    public ApiResponse<List<TextbookSystem>> listTextbooks() {
        return ApiResponse.success(textbookService.listTextbooks());
    }

    @GetMapping("/{textbookId}/grades")
    public ApiResponse<List<Grade>> listGrades(@PathVariable Long textbookId) {
        return ApiResponse.success(textbookService.listGrades(textbookId));
    }

    @GetMapping("/grades/{gradeId}/units")
    public ApiResponse<List<Unit>> listUnits(@PathVariable Long gradeId) {
        return ApiResponse.success(textbookService.listUnits(gradeId));
    }
}
