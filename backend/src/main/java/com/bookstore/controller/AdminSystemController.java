package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.CarouselSaveRequest;
import com.bookstore.service.SystemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class AdminSystemController {

    private final SystemService systemService;

    @GetMapping("/carousel/list")
    public ApiResponse<?> listCarousels() {
        return ApiResponse.success(systemService.listAllCarousels());
    }

    @PostMapping("/carousel")
    public ApiResponse<Void> createCarousel(@Valid @RequestBody CarouselSaveRequest request) {
        systemService.createCarousel(request);
        return ApiResponse.success();
    }

    @PutMapping("/carousel/{id}")
    public ApiResponse<Void> updateCarousel(@PathVariable Long id, @Valid @RequestBody CarouselSaveRequest request) {
        systemService.updateCarousel(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/carousel/{id}")
    public ApiResponse<Void> deleteCarousel(@PathVariable Long id) {
        systemService.deleteCarousel(id);
        return ApiResponse.success();
    }
}
