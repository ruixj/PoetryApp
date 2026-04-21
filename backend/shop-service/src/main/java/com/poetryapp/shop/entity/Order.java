package com.poetryapp.shop.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data @Entity @Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_no", nullable = false, unique = true, length = 50)
    private String orderNo;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;
    @Column(name = "shipping_name", length = 100)
    private String shippingName;
    @Column(name = "shipping_phone", length = 11)
    private String shippingPhone;
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;
    @Column(length = 20)
    private String status = "PENDING"; // PENDING|PROCESSING|SHIPPED|COMPLETED|CANCELLED
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
