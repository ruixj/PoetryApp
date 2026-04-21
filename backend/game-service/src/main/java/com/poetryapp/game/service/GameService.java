package com.poetryapp.game.service;

import com.poetryapp.common.exception.BusinessException;
import com.poetryapp.game.dto.SubmissionDisplay;
import com.poetryapp.game.dto.SubmitPoemRequest;
import com.poetryapp.game.entity.*;
import com.poetryapp.game.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class GameService {

    private static final int GAME_POINTS = 5;

    private final GameSubmissionRepository submissionRepo;
    private final PoemRefRepository poemRepo;
    private final PoemCategoryRefRepository categoryRepo;
    private final UserRefRepository userRepo;

    /**
     * 提交游戏古诗
     * 1. 在 poems 表中按标题/内容匹配古诗
     * 2. 验证古诗属于当前类别
     * 3. 去重（同用户同诗同类别）
     */
    @Transactional
    public Map<String, Object> submitPoem(Long userId, SubmitPoemRequest req) {
        String input = req.getInputText().trim();

        // 在数据库中匹配古诗（按标题或内容完整匹配）
        PoemRef poem = findPoemByInput(input);
        if (poem == null) {
            return Map.of("valid", false, "message", "未找到对应的古诗，请检查输入是否正确");
        }

        // 验证古诗属于该分类
        boolean belongsToCategory = !categoryRepo.findByPoemIdAndCategoryTypeAndCategoryValue(
                poem.getId(), req.getCategoryType(), req.getCategoryValue()).isEmpty();
        if (!belongsToCategory) {
            return Map.of("valid", false, "message",
                    "这首古诗不属于当前分类「" + req.getCategoryValue() + "」，换一首试试吧！");
        }

        // 检查是否已经提交过
        boolean alreadySubmitted = submissionRepo
                .existsByUserIdAndPoemIdAndGameCategoryTypeAndGameCategoryValueAndIsValidTrue(
                        userId, poem.getId(), req.getCategoryType(), req.getCategoryValue());
        if (alreadySubmitted) {
            return Map.of("valid", false, "message", "你已经用过这首古诗了，换一首试试！");
        }

        // 保存有效提交
        GameSubmission sub = new GameSubmission();
        sub.setUserId(userId);
        sub.setPoemId(poem.getId());
        sub.setGameCategoryType(req.getCategoryType());
        sub.setGameCategoryValue(req.getCategoryValue());
        sub.setInputText(input);
        sub.setIsValid(true);
        sub.setPointsEarned(GAME_POINTS);
        submissionRepo.save(sub);

        // 加积分
        userRepo.addPoints(userId, GAME_POINTS);

        log.info("游戏提交成功: userId={}, poemId={}, category={}/{}", userId, poem.getId(),
                req.getCategoryType(), req.getCategoryValue());

        return Map.of("valid", true, "message", "太棒了！+5元宝！",
                "poem", Map.of("title", poem.getTitle(), "author", poem.getAuthor(),
                        "content", poem.getContent()),
                "pointsEarned", GAME_POINTS);
    }

    /**
     * 获取分类下的游戏展示列表（最近50条有效提交）
     */
    public List<SubmissionDisplay> getSubmissions(String categoryType, String categoryValue) {
        return submissionRepo
                .findByGameCategoryTypeAndGameCategoryValueAndIsValidTrueOrderByCreatedAtDesc(
                        categoryType, categoryValue)
                .stream()
                .limit(50)
                .map(this::toDisplay)
                .collect(Collectors.toList());
    }

    private PoemRef findPoemByInput(String input) {
        // 方案1：按标题匹配
        return poemRepo.findAll().stream()
                .filter(p -> p.getTitle().equals(input) || p.getContent().contains(input)
                        || input.contains(p.getTitle()))
                .findFirst()
                .orElse(null);
    }

    private SubmissionDisplay toDisplay(GameSubmission sub) {
        SubmissionDisplay d = new SubmissionDisplay();
        d.setSubmissionId(sub.getId());
        d.setUserId(sub.getUserId());
        d.setCategoryType(sub.getGameCategoryType());
        d.setCategoryValue(sub.getGameCategoryValue());
        d.setCreatedAt(sub.getCreatedAt() != null ? sub.getCreatedAt().toString() : "");

        userRepo.findById(sub.getUserId()).ifPresent(u -> {
            d.setNickname(u.getNickname());
            d.setAvatarUrl(u.getAvatarUrl());
        });
        poemRepo.findById(sub.getPoemId()).ifPresent(p -> {
            d.setPoemId(p.getId());
            d.setPoemTitle(p.getTitle());
            d.setPoemAuthor(p.getAuthor());
            d.setPoemContent(p.getContent());
        });
        return d;
    }
}
