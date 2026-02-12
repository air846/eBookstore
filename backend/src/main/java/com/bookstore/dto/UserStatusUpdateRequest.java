package com.bookstore.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
// 用户状态更新请求参数
public class UserStatusUpdateRequest {

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态非法")
    @Max(value = 1, message = "状态非法")
    private Integer status;
}
