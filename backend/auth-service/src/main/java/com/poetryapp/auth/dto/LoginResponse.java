package com.poetryapp.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private String role;
    /** true = 首次登录，跳转个人设置页；false = 进入学习页 */
    private Boolean isFirstLogin;
}
