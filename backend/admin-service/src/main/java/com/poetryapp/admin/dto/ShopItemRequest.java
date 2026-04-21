package com.poetryapp.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShopItemRequest {
    @NotBlank private String name;
    private String description;
    private String imageUrl;
    @NotNull @Min(1) private Integer pointsCost;
    @NotNull @Min(0) private Integer stock;
}
