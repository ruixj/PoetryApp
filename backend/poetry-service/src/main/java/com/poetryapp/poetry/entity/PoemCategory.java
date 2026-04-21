package com.poetryapp.poetry.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data @Entity @Table(name = "poem_categories")
public class PoemCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "poem_id", nullable = false)
    private Long poemId;
    @Column(name = "category_type", nullable = false, length = 50)
    private String categoryType; // SOLAR_TERM | THEME | POET | FLYING_FLOWER
    @Column(name = "category_value", nullable = false, length = 100)
    private String categoryValue;
}
