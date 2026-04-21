package com.poetryapp.shop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaceOrderRequest {
    @NotBlank(message = "收件人姓名不能为空")
    private String shippingName;
    @NotBlank(message = "收件人电话不能为空")
    private String shippingPhone;
    @NotBlank(message = "收货地址不能为空")
    private String shippingAddress;
}
