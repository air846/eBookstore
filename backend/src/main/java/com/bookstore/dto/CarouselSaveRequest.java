package com.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
// 轮播图保存请求参数
public class CarouselSaveRequest {

    @NotBlank(message = "图片地址不能为空")
    private String imageUrl;
    private String link;
    private Integer sortOrder = 0;
    private Integer status = 1;
}
