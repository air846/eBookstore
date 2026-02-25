package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.common.UserContext;
import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.MiniLoginRequest;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.dto.UserInfoUpdateRequest;
import com.bookstore.dto.UserPasswordUpdateRequest;
import com.bookstore.service.AuthService;
import com.bookstore.vo.FileUploadVO;
import com.bookstore.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success();
    }

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.userLogin(request));
    }

    @PostMapping("/mp/login")
    public ApiResponse<LoginVO> miniLogin(@Valid @RequestBody MiniLoginRequest request) {
        return ApiResponse.success(authService.miniLogin(request));
    }

    @GetMapping("/info")
    public ApiResponse<?> info() {
        return ApiResponse.success(authService.userInfo(UserContext.getUserId()));
    }

    @PutMapping("/info")
    public ApiResponse<?> updateInfo(@Valid @RequestBody UserInfoUpdateRequest request) {
        return ApiResponse.success(authService.updateUserInfo(UserContext.getUserId(), request));
    }

    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(@Valid @RequestBody UserPasswordUpdateRequest request) {
        authService.updatePassword(UserContext.getUserId(), request);
        return ApiResponse.success();
    }

    @PostMapping("/avatar")
    public ApiResponse<FileUploadVO> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(authService.uploadAvatar(UserContext.getUserId(), file));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success();
    }
}
