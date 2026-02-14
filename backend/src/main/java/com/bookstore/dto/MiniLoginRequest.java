package com.bookstore.dto;

import lombok.Data;

@Data
public class MiniLoginRequest {

    // 兼容旧调用：可直接传 unionId。
    private String unionId;

    // 微信真实登录：前端 wx.login 获取 code 后传入。
    private String code;

    private String nickname;

    private String avatar;
}
