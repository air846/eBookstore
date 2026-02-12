package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carousel")
@RequiredArgsConstructor
public class CarouselController {

    private final SystemService systemService;

    @GetMapping("/list")
    public ApiResponse<?> list() {
        return ApiResponse.success(systemService.listEnabledCarousels());
    }
}
