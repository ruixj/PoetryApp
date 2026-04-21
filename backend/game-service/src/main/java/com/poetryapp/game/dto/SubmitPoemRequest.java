package com.poetryapp.game.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmitPoemRequest {
    @NotBlank(message = "类别类型不能为空")
    private String categoryType;
    @NotBlank(message = "类别值不能为空")
    private String categoryValue;
    @NotBlank(message = "古诗内容不能为空")
    private String inputText;
}
