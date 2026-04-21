package com.poetryapp.shop.entity;

import lombok.Data;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long itemId;
    private String itemName;
    private String itemImage;
    private Integer quantity;
    private Integer pointsCost;
}
