package com.poetryapp.game.controller;

import com.poetryapp.common.exception.GlobalExceptionHandler;
import com.poetryapp.common.response.ApiResponse;
import com.poetryapp.game.dto.SubmissionDisplay;
import com.poetryapp.game.dto.SubmitPoemRequest;
import com.poetryapp.game.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Import(GlobalExceptionHandler.class)
public class GameController {

    private final GameService gameService;

    /** 提交古诗（文字或语音识别后文本） */
    @PostMapping("/submit")
    public ApiResponse<Map<String, Object>> submit(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody SubmitPoemRequest req) {
        return ApiResponse.success(gameService.submitPoem(userId, req));
    }

    /** 获取某分类已有的有效提交列表（展示给所有用户） */
    @GetMapping("/submissions")
    public ApiResponse<List<SubmissionDisplay>> getSubmissions(
            @RequestParam String categoryType,
            @RequestParam String categoryValue) {
        return ApiResponse.success(gameService.getSubmissions(categoryType, categoryValue));
    }
}
