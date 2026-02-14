package com.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserInfoUpdateRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 100, message = "昵称长度不能超过100")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;

    @Size(max = 255, message = "头像地址长度不能超过255")
    private String avatar;
}
