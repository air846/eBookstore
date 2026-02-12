package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.service.UrgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/urge")
@RequiredArgsConstructor
public class AdminUrgeController {

    private final UrgeService urgeService;

    @GetMapping("/stats")
    public ApiResponse<?> stats() {
        return ApiResponse.success(urgeService.listStats());
    }
}
