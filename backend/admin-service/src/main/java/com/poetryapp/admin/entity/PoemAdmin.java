package com.poetryapp.admin.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PoemAdmin {
    private Long id;
    private String title;
    private String author;
    private String dynasty;
    private String content;
    private String pinyin;
    private String translation;
    private String background;
    private String authorIntro;
    private String animationUrl;
    private String mindmapData;
    private String audioUrl;
    private String difficultyWords;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
