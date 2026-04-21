package com.poetryapp.poetry.entity;

import lombok.Data;

@Data
public class Unit {
    private Long id;
    private String name;
    private Long gradeId;
    private Integer orderNum = 0;
}
