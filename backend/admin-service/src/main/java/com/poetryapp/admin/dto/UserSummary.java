package com.poetryapp.admin.dto;

import lombok.Data;

@Data
public class UserSummary {
    private Long id;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private Integer yuanbaoPoints;
    private Integer totalStudyMinutes;
    private String role;
    private String createdAt;
}
