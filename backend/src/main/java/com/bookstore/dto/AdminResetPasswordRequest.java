package com.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminResetPasswordRequest {

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度应为6-20")
    private String newPassword;
}
