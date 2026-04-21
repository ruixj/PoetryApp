package com.poetryapp.poetry.entity;

import lombok.Data;

@Data
public class PoemCategory {
    private Long id;
    private Long poemId;
    private String categoryType;
    private String categoryValue;
}
