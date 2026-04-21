package com.poetryapp.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnitRequest {
    @NotNull private Long gradeId;
    @NotBlank private String name;
    private Integer orderNum = 0;
}
