package com.poetryapp.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeRequest {
    @NotNull private Long textbookId;
    @NotBlank private String name;
    @NotBlank private String level; // PRIMARY | MIDDLE
    @NotNull private Integer gradeNumber;
    private Integer orderNum = 0;
}
