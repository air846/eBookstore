package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminService adminService;

    @GetMapping
    public ApiResponse<?> dashboard() {
        return ApiResponse.success(adminService.dashboard());
    }
}
