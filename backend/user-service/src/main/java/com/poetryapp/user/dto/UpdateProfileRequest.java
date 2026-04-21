package com.poetryapp.user.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String nickname;
    private Long textbookId;
    private Long gradeId;
}
