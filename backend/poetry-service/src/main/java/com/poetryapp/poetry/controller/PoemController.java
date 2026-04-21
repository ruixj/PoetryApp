package com.poetryapp.poetry.controller;

import com.poetryapp.common.response.ApiResponse;
import com.poetryapp.poetry.entity.Poem;
import com.poetryapp.poetry.entity.PoemCategory;
import com.poetryapp.poetry.entity.UserPoemProgress;
import com.poetryapp.poetry.service.LearningService;
import com.poetryapp.poetry.service.PoemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/poetry")
@RequiredArgsConstructor
public class PoemController {

    private final PoemService poemService;
    private final LearningService learningService;

    /** 获取古诗详情 */
    @GetMapping("/poems/{poemId}")
    public ApiResponse<Poem> getPoem(@PathVariable Long poemId) {
        return ApiResponse.success(poemService.getPoem(poemId));
    }

    /** 获取单元下的古诗列表 */
    @GetMapping("/units/{unitId}/poems")
    public ApiResponse<List<Poem>> getPoemsByUnit(@PathVariable Long unitId) {
        return ApiResponse.success(poemService.getPoemsByUnit(unitId));
    }

    /** 获取分类下的古诗列表 */
    @GetMapping("/poems/category")
    public ApiResponse<List<Poem>> getPoemsByCategory(
            @RequestParam String type,
            @RequestParam String value) {
        return ApiResponse.success(poemService.getPoemsByCategory(type, value));
    }

    /** 获取分类可选值 */
    @GetMapping("/categories/values")
    public ApiResponse<List<String>> getCategoryValues(@RequestParam String type) {
        return ApiResponse.success(poemService.getCategoryValues(type));
    }

    /** 获取古诗分类标签 */
    @GetMapping("/poems/{poemId}/categories")
    public ApiResponse<List<PoemCategory>> getPoemCategories(@PathVariable Long poemId) {
        return ApiResponse.success(poemService.getPoemCategories(poemId));
    }

    // ── 学习库 ───────────────────────────────────────────

    /** 将整个单元加入学习库 */
    @PostMapping("/library/unit/{unitId}")
    public ApiResponse<Void> addUnitToLibrary(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long unitId) {
        learningService.addUnitToLibrary(userId, unitId);
        return ApiResponse.success("已添加到学习库", null);
    }

    /** 将单首诗加入学习库 */
    @PostMapping("/library/poem/{poemId}")
    public ApiResponse<Void> addPoemToLibrary(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long poemId) {
        learningService.addPoemToLibrary(userId, poemId);
        return ApiResponse.success("已添加到学习库", null);
    }

    /** 获取学习库诗 ID 列表 */
    @GetMapping("/library")
    public ApiResponse<List<Long>> getLibrary(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(learningService.getLibraryPoemIds(userId));
    }

    // ── 学习进度 ─────────────────────────────────────────

    /** 更新学习阶段 */
    @PostMapping("/progress/{poemId}/stage")
    public ApiResponse<Map<String, Object>> updateStage(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long poemId,
            @RequestParam String stage) {
        var result = learningService.updateStage(userId, poemId, stage);
        return ApiResponse.success(Map.of(
                "justCompleted",  result.justCompleted(),
                "totalCompleted", result.totalCompleted(),
                "level",          result.level()
        ));
    }

    /** 获取单首诗进度 */
    @GetMapping("/progress/{poemId}")
    public ApiResponse<UserPoemProgress> getProgress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long poemId) {
        return ApiResponse.success(learningService.getProgress(userId, poemId));
    }

    /** 获取已完成的古诗（带录音 URL，用于回放） */
    @GetMapping("/progress/completed")
    public ApiResponse<List<UserPoemProgress>> getCompleted(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(learningService.getCompletedPoems(userId));
    }

    /** 上传背诵录音 */
    @PostMapping("/progress/{poemId}/recording")
    public ApiResponse<String> uploadRecording(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long poemId,
            @RequestParam("file") MultipartFile file) throws IOException {
        String url = poemService.uploadRecording(userId, poemId, file);
        learningService.saveRecordingUrl(userId, poemId, url);
        return ApiResponse.success("录音上传成功", url);
    }

    /** 获取已完成古诗数（供 user-service 调用） */
    @GetMapping("/stats/completed-count")
    public ApiResponse<Integer> completedCount(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(learningService.countCompleted(userId));
    }
}
