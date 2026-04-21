package com.poetryapp.game.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GameSubmission {
    private Long id;
    private Long userId;
    private Long poemId;
    private String gameCategoryType;
    private String gameCategoryValue;
    private String inputText;
    private Boolean isValid = false;
    private Integer pointsEarned = 0;
    private LocalDateTime createdAt;
}
