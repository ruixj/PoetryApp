package com.poetryapp.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PoemRequest {
    @NotBlank private String title;
    private String author;
    private String dynasty;
    @NotBlank private String content;
    private String pinyin;
    private String translation;
    private String background;
    private String authorIntro;
    private String mindmapData;
    private String difficultyWords;
}
