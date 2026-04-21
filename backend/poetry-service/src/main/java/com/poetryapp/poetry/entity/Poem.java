package com.poetryapp.poetry.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data @Entity @Table(name = "poems")
public class Poem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(length = 100)
    private String author;
    @Column(length = 50)
    private String dynasty;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String pinyin;
    @Column(columnDefinition = "TEXT")
    private String translation;
    @Column(columnDefinition = "TEXT")
    private String background;
    @Column(name = "author_intro", columnDefinition = "TEXT")
    private String authorIntro;
    @Column(name = "animation_url", length = 500)
    private String animationUrl;
    @Column(name = "mindmap_data", columnDefinition = "TEXT")
    private String mindmapData;
    @Column(name = "audio_url", length = 500)
    private String audioUrl;
    @Column(name = "difficulty_words", columnDefinition = "TEXT")
    private String difficultyWords;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
