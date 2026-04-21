package com.poetryapp.auth.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoginRecord {
    private Long id;
    private Long userId;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private Integer durationMinutes = 0;
}
