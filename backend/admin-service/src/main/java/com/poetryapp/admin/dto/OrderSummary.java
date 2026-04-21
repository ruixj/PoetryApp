package com.poetryapp.admin.dto;

import lombok.Data;

@Data
public class OrderSummary {
    private Long id;
    private String orderNo;
    private Long userId;
    private String userNickname;
    private Integer totalPoints;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String status;
    private String createdAt;
}
