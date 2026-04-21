package com.poetryapp.shop.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CartItem {
    private Long id;
    private Long userId;
    private Long itemId;
    private Integer quantity = 1;
    private LocalDateTime createdAt;
}
