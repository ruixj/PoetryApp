package com.poetryapp.poetry.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserPoemLibrary {
    private Long id;
    private Long userId;
    private Long poemId;
    private LocalDateTime addedAt = LocalDateTime.now();
}
