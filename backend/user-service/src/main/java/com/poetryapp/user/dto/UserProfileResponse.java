package com.poetryapp.user.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private Integer yuanbaoPoints;
    private Integer totalStudyMinutes;
    private String role;
    private Boolean isFirstLogin;
    private Long textbookId;
    private Long gradeId;
    /** 学习等级：童生/秀才/举人/贡士/进士 */
    private String studyLevel;
    /** 已完成古诗数 */
    private Integer completedPoems;
}
