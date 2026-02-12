package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public ApiResponse<?> list(@RequestParam(defaultValue = "1") long page,
                               @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.success(commentService.adminList(page, size));
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> delete(@PathVariable Long commentId) {
        commentService.adminDelete(commentId);
        return ApiResponse.success();
    }
}
