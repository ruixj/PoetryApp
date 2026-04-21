package com.poetryapp.auth.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String phone;
    private String password;
    private String nickname;
    private String avatarUrl;
    private Integer yuanbaoPoints = 0;
    private Integer totalStudyMinutes = 0;
    private String role = "USER";
    private Boolean isFirstLogin = true;
    private Long textbookId;
    private Long gradeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
