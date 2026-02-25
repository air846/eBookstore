package com.bookstore.service;

import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.MiniLoginRequest;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.dto.UserInfoUpdateRequest;
import com.bookstore.dto.UserPasswordUpdateRequest;
import com.bookstore.entity.User;
import com.bookstore.vo.FileUploadVO;
import com.bookstore.vo.LoginVO;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {

    LoginVO userLogin(LoginRequest request);

    LoginVO adminLogin(LoginRequest request);

    LoginVO miniLogin(MiniLoginRequest request);

    void register(RegisterRequest request);

    User userInfo(Long userId);

    User updateUserInfo(Long userId, UserInfoUpdateRequest request);

    void updatePassword(Long userId, UserPasswordUpdateRequest request);

    FileUploadVO uploadAvatar(Long userId, MultipartFile file);
}
