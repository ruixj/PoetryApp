package com.poetryapp.game.entity;

import lombok.Data;

@Data
public class PoemRef {
    private Long id;
    private String title;
    private String author;
    private String dynasty;
    private String content;
}
