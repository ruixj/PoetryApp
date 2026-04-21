package com.poetryapp.game.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data @Entity @Table(name = "game_submissions")
public class GameSubmission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "poem_id", nullable = false)
    private Long poemId;
    @Column(name = "game_category_type", length = 50)
    private String gameCategoryType;
    @Column(name = "game_category_value", length = 100)
    private String gameCategoryValue;
    @Column(name = "input_text", columnDefinition = "TEXT")
    private String inputText;
    @Column(name = "is_valid")
    private Boolean isValid = false;
    @Column(name = "points_earned")
    private Integer pointsEarned = 0;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
