package com.poetryapp.poetry.entity;

import lombok.Data;

@Data
public class Grade {
    private Long id;
    private String name;
    private Long textbookId;
    private Integer orderNum = 0;
}
