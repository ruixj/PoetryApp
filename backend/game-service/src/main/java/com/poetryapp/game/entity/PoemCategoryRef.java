package com.poetryapp.game.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data @Entity @Table(name = "poem_categories")
public class PoemCategoryRef {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "poem_id", nullable = false)
    private Long poemId;
    @Column(name = "category_type", nullable = false, length = 50)
    private String categoryType;
    @Column(name = "category_value", nullable = false, length = 100)
    private String categoryValue;
}
