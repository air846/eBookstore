package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.AdminResetPasswordRequest;
import com.bookstore.dto.UserStatusUpdateRequest;
import com.bookstore.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminService adminService;

    @GetMapping("/list")
    public ApiResponse<?> list(@RequestParam(defaultValue = "1") long page,
                               @RequestParam(defaultValue = "10") long size,
                               @RequestParam(required = false) String keyword) {
        return ApiResponse.success(adminService.pageUsers(page, size, keyword));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody UserStatusUpdateRequest request) {
        adminService.updateUserStatus(id, request.getStatus());
        return ApiResponse.success();
    }

    @PutMapping("/{id}/password/reset")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody AdminResetPasswordRequest request) {
        adminService.resetUserPassword(id, request.getNewPassword());
        return ApiResponse.success();
    }
}
