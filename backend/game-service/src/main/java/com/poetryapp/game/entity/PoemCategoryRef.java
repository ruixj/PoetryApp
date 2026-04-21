package com.poetryapp.game.entity;

import lombok.Data;

@Data
public class PoemCategoryRef {
    private Long id;
    private Long poemId;
    private String categoryType;
    private String categoryValue;
}
