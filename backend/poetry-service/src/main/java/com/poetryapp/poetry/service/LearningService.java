package com.poetryapp.poetry.service;

import com.poetryapp.common.exception.BusinessException;
import com.poetryapp.poetry.entity.*;
import com.poetryapp.poetry.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 学习服务：管理学习库、学习进度、积分
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class LearningService {

    private final UserPoemLibraryRepository libraryRepo;
    private final UserPoemProgressRepository progressRepo;
    private final PoemRepository poemRepo;

    // ── 学习库 ─────────────────────────────────────
    /** 将单元所有诗加入学习库 */
    @Transactional
    public void addUnitToLibrary(Long userId, Long unitId) {
        List<Poem> poems = poemRepo.findByUnitId(unitId);
        for (Poem poem : poems) {
            if (!libraryRepo.existsByUserIdAndPoemId(userId, poem.getId())) {
                UserPoemLibrary lib = new UserPoemLibrary();
                lib.setUserId(userId);
                lib.setPoemId(poem.getId());
                libraryRepo.insert(lib);
            }
        }
    }

    /** 将单首诗加入学习库 */
    @Transactional
    public void addPoemToLibrary(Long userId, Long poemId) {
        if (!poemRepo.existsById(poemId)) throw new BusinessException("古诗不存在");
        if (!libraryRepo.existsByUserIdAndPoemId(userId, poemId)) {
            UserPoemLibrary lib = new UserPoemLibrary();
            lib.setUserId(userId);
            lib.setPoemId(poemId);
            libraryRepo.insert(lib);
        }
    }

    /** 获取用户学习库（含进度） */
    public List<Long> getLibraryPoemIds(Long userId) {
        return libraryRepo.findPoemIdsByUserId(userId);
    }

    // ── 学习进度 ───────────────────────────────────
    /** 更新学习阶段（LISTEN→READ→UNDERSTAND→ANALYZE→MEMORIZE→COMPLETED） */
    @Transactional
    public UpdateStageResult updateStage(Long userId, Long poemId, String stage) {
        UserPoemProgress progress = progressRepo.findByUserIdAndPoemId(userId, poemId)
                .orElseGet(() -> {
                    UserPoemProgress p = new UserPoemProgress();
                    p.setUserId(userId);
                    p.setPoemId(poemId);
                    return p;
                });

        progress.setCurrentStage(stage);

        boolean justCompleted = false;
        if ("COMPLETED".equals(stage) && !Boolean.TRUE.equals(progress.getIsCompleted())) {
            progress.setIsCompleted(true);
            progress.setCompletedAt(java.time.LocalDateTime.now());
            justCompleted = true;
        }
        if (progress.getId() == null) {
            progressRepo.insert(progress);
        } else {
            progressRepo.update(progress);
        }

        int totalCompleted = progressRepo.countCompletedByUserId(userId);
        String level = calcLevel(totalCompleted);

        log.info("学习进度更新: userId={}, poemId={}, stage={}, completed={}", userId, poemId, stage, justCompleted);

        return new UpdateStageResult(justCompleted, totalCompleted, level);
    }

    /** 保存背诵录音 URL */
    @Transactional
    public void saveRecordingUrl(Long userId, Long poemId, String url) {
        UserPoemProgress progress = progressRepo.findByUserIdAndPoemId(userId, poemId)
                .orElseThrow(() -> new BusinessException("进度记录不存在"));
        progress.setRecordingUrl(url);
        progressRepo.update(progress);
    }

    /** 获取已完成的古诗（用于个人页面回放） */
    public List<UserPoemProgress> getCompletedPoems(Long userId) {
        return progressRepo.findByUserIdAndIsCompletedTrueOrderByCompletedAtDesc(userId);
    }

    /** 获取单首诗的进度 */
    public UserPoemProgress getProgress(Long userId, Long poemId) {
        return progressRepo.findByUserIdAndPoemId(userId, poemId).orElse(null);
    }

    public int countCompleted(Long userId) {
        return progressRepo.countCompletedByUserId(userId);
    }

    private String calcLevel(int count) {
        if (count < 10)  return "童生";
        if (count < 30)  return "秀才";
        if (count < 50)  return "举人";
        if (count < 70)  return "贡士";
        return "进士";
    }

    public record UpdateStageResult(boolean justCompleted, int totalCompleted, String level) {}
}
