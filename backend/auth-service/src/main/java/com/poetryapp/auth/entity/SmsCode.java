package com.poetryapp.auth.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SmsCode {
    private Long id;
    private String phone;
    private String code;
    private LocalDateTime expiresAt;
    private Boolean isUsed = false;
    private LocalDateTime createdAt;
}
