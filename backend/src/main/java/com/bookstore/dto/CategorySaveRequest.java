package com.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
// 分类保存请求参数
public class CategorySaveRequest {

    @NotBlank(message = "分类名不能为空")
    private String name;
    private Long parentId = 0L;
    private Integer sortOrder = 0;
}
