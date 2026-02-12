package com.bookstore.service;

import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.entity.User;
import com.bookstore.vo.LoginVO;

public interface AuthService {

    LoginVO userLogin(LoginRequest request);

    LoginVO adminLogin(LoginRequest request);

    void register(RegisterRequest request);

    User userInfo(Long userId);
}
