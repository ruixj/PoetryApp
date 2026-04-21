package com.poetryapp.game.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data @Entity @Table(name = "users")
public class UserRef {
    @Id private Long id;
    @Column(length = 50)
    private String nickname;
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    @Column(name = "yuanbao_points")
    private Integer yuanbaoPoints = 0;
}
