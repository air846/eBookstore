package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.BookQueryRequest;
import com.bookstore.dto.BookSaveRequest;
import com.bookstore.dto.ChapterSaveRequest;
import com.bookstore.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/book")
@RequiredArgsConstructor
public class AdminBookController {

    private final BookService bookService;

    @GetMapping("/list")
    public ApiResponse<?> list(@ModelAttribute BookQueryRequest request) {
        return ApiResponse.success(bookService.pageBooks(request, true));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody BookSaveRequest request) {
        bookService.create(request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody BookSaveRequest request) {
        bookService.update(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        bookService.updateStatus(id, status);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ApiResponse.success();
    }

    @GetMapping("/{bookId}/chapters")
    public ApiResponse<?> chapterList(@PathVariable Long bookId) {
        return ApiResponse.success(bookService.listChapters(bookId));
    }

    @PostMapping("/{bookId}/chapters")
    public ApiResponse<Void> createChapter(@PathVariable Long bookId, @Valid @RequestBody ChapterSaveRequest request) {
        bookService.createChapter(bookId, request);
        return ApiResponse.success();
    }

    @PutMapping("/{bookId}/chapters/{chapterId}")
    public ApiResponse<Void> updateChapter(@PathVariable Long bookId,
                                           @PathVariable Long chapterId,
                                           @Valid @RequestBody ChapterSaveRequest request) {
        bookService.updateChapter(bookId, chapterId, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{bookId}/chapters/{chapterId}")
    public ApiResponse<Void> deleteChapter(@PathVariable Long bookId, @PathVariable Long chapterId) {
        bookService.deleteChapter(bookId, chapterId);
        return ApiResponse.success();
    }
}
