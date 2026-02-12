package com.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategorySaveRequest {

    @NotBlank(message = "分类名不能为空")
    private String name;
    private Long parentId = 0L;
    private Integer sortOrder = 0;
}
