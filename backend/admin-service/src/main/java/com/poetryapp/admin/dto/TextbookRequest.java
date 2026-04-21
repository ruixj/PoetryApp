package com.poetryapp.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TextbookRequest {
    @NotBlank private String name;
    private String description;
    private Integer orderNum = 0;
}
