package com.poetryapp.poetry.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class UnitPoemId implements Serializable {
    private Long unitId;
    private Long poemId;
}
