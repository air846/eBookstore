package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.common.UserContext;
import com.bookstore.dto.BookQueryRequest;
import com.bookstore.dto.HistorySaveRequest;
import com.bookstore.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/list")
    public ApiResponse<?> list(@ModelAttribute BookQueryRequest request) {
        return ApiResponse.success(bookService.pageBooks(request, false));
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<?> detail(@PathVariable Long id) {
        return ApiResponse.success(bookService.detail(id));
    }

    @GetMapping("/read/{id}")
    public ApiResponse<String> read(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success(bookService.readBook(UserContext.getUserId(), request.getRemoteAddr(), id));
    }

    @PostMapping("/favorite/{bookId}")
    public ApiResponse<Void> favorite(@PathVariable Long bookId) {
        bookService.favorite(UserContext.getUserId(), bookId);
        return ApiResponse.success();
    }

    @GetMapping("/favorite/list")
    public ApiResponse<?> favoriteList() {
        return ApiResponse.success(bookService.favoriteList(UserContext.getUserId()));
    }

    @PostMapping("/history")
    public ApiResponse<Void> saveHistory(@Valid @RequestBody HistorySaveRequest request) {
        bookService.saveHistory(UserContext.getUserId(), request);
        return ApiResponse.success();
    }

    @GetMapping("/history/list")
    public ApiResponse<?> historyList() {
        return ApiResponse.success(bookService.historyList(UserContext.getUserId()));
    }
}
