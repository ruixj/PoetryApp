package com.poetryapp.game.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data @Entity @Table(name = "poems")
public class PoemRef {
    @Id private Long id;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(length = 100)
    private String author;
    @Column(length = 50)
    private String dynasty;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
