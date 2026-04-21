package com.poetryapp.shop.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data @Entity @Table(name = "users")
public class UserShopRef {
    @Id private Long id;
    @Column(name = "yuanbao_points")
    private Integer yuanbaoPoints = 0;
}
