package com.poetryapp.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 11)
    private String phone;
    @Column(nullable = false)
    private String password;
    @Column(length = 50)
    private String nickname;
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    @Column(name = "yuanbao_points")
    private Integer yuanbaoPoints = 0;
    @Column(name = "total_study_minutes")
    private Integer totalStudyMinutes = 0;
    @Column(length = 20)
    private String role = "USER";
    @Column(name = "is_first_login")
    private Boolean isFirstLogin = true;
    @Column(name = "textbook_id")
    private Long textbookId;
    @Column(name = "grade_id")
    private Long gradeId;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
