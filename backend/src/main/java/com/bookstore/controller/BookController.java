package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.common.UserContext;
import com.bookstore.dto.BookQueryRequest;
import com.bookstore.dto.CommentCreateRequest;
import com.bookstore.dto.CommentReactionRequest;
import com.bookstore.dto.HistorySaveRequest;
import com.bookstore.entity.BookChapter;
import com.bookstore.service.CommentService;
import com.bookstore.service.BookService;
import com.bookstore.service.UrgeService;
import com.bookstore.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final CommentService commentService;
    private final UrgeService urgeService;
    private final NoticeService noticeService;

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

    @GetMapping("/{id}/chapters")
    public ApiResponse<List<BookChapter>> chapters(@PathVariable Long id) {
        return ApiResponse.success(bookService.listChapters(id));
    }

    @GetMapping("/{bookId}/chapter/{chapterId}/comments")
    public ApiResponse<?> listComments(@PathVariable Long bookId,
                                       @PathVariable Long chapterId,
                                       @RequestParam Integer paragraphIndex,
                                       @RequestParam(required = false, defaultValue = "time") String sortBy) {
        return ApiResponse.success(commentService.listComments(UserContext.getUserId(), bookId, chapterId, paragraphIndex, sortBy));
    }

    @GetMapping("/{bookId}/chapter/{chapterId}/comment-counts")
    public ApiResponse<?> commentCounts(@PathVariable Long bookId,
                                        @PathVariable Long chapterId) {
        return ApiResponse.success(commentService.commentCounts(bookId, chapterId));
    }

    @PostMapping("/{bookId}/chapter/{chapterId}/comments")
    public ApiResponse<Void> createComment(@PathVariable Long bookId,
                                           @PathVariable Long chapterId,
                                           @Valid @RequestBody CommentCreateRequest request) {
        commentService.createComment(UserContext.getUserId(), bookId, chapterId, request);
        return ApiResponse.success();
    }

    @PostMapping("/comments/{commentId}/reaction")
    public ApiResponse<Void> react(@PathVariable Long commentId,
                                   @Valid @RequestBody CommentReactionRequest request) {
        commentService.react(UserContext.getUserId(), commentId, request);
        return ApiResponse.success();
    }

    @PostMapping("/comments/{commentId}/hide")
    public ApiResponse<Void> hide(@PathVariable Long commentId) {
        commentService.hide(UserContext.getUserId(), commentId);
        return ApiResponse.success();
    }

    @DeleteMapping("/comments/{commentId}/hide")
    public ApiResponse<Void> unhide(@PathVariable Long commentId) {
        commentService.unhide(UserContext.getUserId(), commentId);
        return ApiResponse.success();
    }

    @GetMapping("/comments/hidden")
    public ApiResponse<?> hidden(@RequestParam Long chapterId) {
        return ApiResponse.success(commentService.hiddenIds(UserContext.getUserId(), chapterId));
    }

    @PostMapping("/{bookId}/urge")
    public ApiResponse<Void> urge(@PathVariable Long bookId) {
        urgeService.create(UserContext.getUserId(), bookId);
        return ApiResponse.success();
    }

    @GetMapping("/notice/list")
    public ApiResponse<?> noticeList(@RequestParam(defaultValue = "1") long page,
                                     @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.success(noticeService.list(UserContext.getUserId(), page, size));
    }

    @PostMapping("/notice/read/{id}")
    public ApiResponse<Void> noticeRead(@PathVariable Long id) {
        noticeService.markRead(UserContext.getUserId(), id);
        return ApiResponse.success();
    }

    @PostMapping("/notice/read/all")
    public ApiResponse<Void> noticeReadAll() {
        noticeService.markAllRead(UserContext.getUserId());
        return ApiResponse.success();
    }

    @PostMapping("/favorite/{bookId}")
    public ApiResponse<Void> favorite(@PathVariable Long bookId) {
        bookService.favorite(UserContext.getUserId(), bookId);
        return ApiResponse.success();
    }

    @DeleteMapping("/favorite/{bookId}")
    public ApiResponse<Void> unfavorite(@PathVariable Long bookId) {
        bookService.unfavorite(UserContext.getUserId(), bookId);
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
