package com.poetryapp.poetry.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserPoemProgress {
    private Long id;
    private Long userId;
    private Long poemId;
    private String currentStage = "LISTEN";
    private Boolean isCompleted = false;
    private LocalDateTime completedAt;
    private String recordingUrl;
}
