package com.poetryapp.game.entity;

import lombok.Data;

@Data
public class UserRef {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private Integer yuanbaoPoints = 0;
}
