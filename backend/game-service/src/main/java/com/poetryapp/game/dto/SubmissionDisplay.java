package com.poetryapp.game.dto;

import lombok.Data;

@Data
public class SubmissionDisplay {
    private Long submissionId;
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private Long poemId;
    private String poemTitle;
    private String poemAuthor;
    private String poemContent;
    private String categoryType;
    private String categoryValue;
    private String createdAt;
}
