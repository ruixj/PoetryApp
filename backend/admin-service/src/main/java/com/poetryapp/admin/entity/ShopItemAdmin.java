package com.poetryapp.admin.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShopItemAdmin {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Integer pointsCost;
    private Integer stock = 0;
    private String status = "ON_SHELF";
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
