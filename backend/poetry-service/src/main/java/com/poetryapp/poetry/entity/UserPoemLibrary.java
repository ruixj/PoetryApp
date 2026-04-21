package com.poetryapp.poetry.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Entity @Table(name = "user_poem_library")
public class UserPoemLibrary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "poem_id", nullable = false)
    private Long poemId;
    @Column(name = "added_at")
    private LocalDateTime addedAt = LocalDateTime.now();
}
