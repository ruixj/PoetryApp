package com.poetryapp.poetry.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Entity @Table(name = "user_poem_progress")
public class UserPoemProgress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "poem_id", nullable = false)
    private Long poemId;
    @Column(name = "current_stage", length = 30)
    private String currentStage = "LISTEN"; // LISTEN|READ|UNDERSTAND|ANALYZE|MEMORIZE|COMPLETED
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    @Column(name = "recording_url", length = 500)
    private String recordingUrl;
}
