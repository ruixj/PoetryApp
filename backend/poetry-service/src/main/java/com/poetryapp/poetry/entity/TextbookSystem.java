package com.poetryapp.poetry.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TextbookSystem {
    private Long id;
    private String name;
    private String description;
    private Integer orderNum = 0;
    private LocalDateTime createdAt;
}
