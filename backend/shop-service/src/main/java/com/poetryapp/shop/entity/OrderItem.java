package com.poetryapp.shop.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data @Entity @Table(name = "order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    @Column(name = "item_name", length = 200)
    private String itemName;
    @Column(name = "item_image", length = 500)
    private String itemImage;
    @Column(nullable = false)
    private Integer quantity;
    @Column(name = "points_cost", nullable = false)
    private Integer pointsCost;
}
