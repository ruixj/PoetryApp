package com.poetryapp.poetry.entity;

import lombok.Data;

@Data
public class UnitPoem {
    private Long unitId;
    private Long poemId;
    private Integer orderNum = 0;
}
