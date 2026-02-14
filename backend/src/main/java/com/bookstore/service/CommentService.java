package com.bookstore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookstore.dto.CommentCreateRequest;
import com.bookstore.dto.CommentReactionRequest;
import com.bookstore.vo.AdminCommentVO;
import com.bookstore.vo.CommentVO;

import java.util.List;
import java.util.Map;

public interface CommentService {

    List<CommentVO> listComments(Long userId, Long bookId, Long chapterId, Integer virtualChapterIndex, Integer paragraphIndex, String sortBy);

    Map<Integer, Long> commentCounts(Long bookId, Long chapterId, Integer virtualChapterIndex);

    void createComment(Long userId, Long bookId, Long chapterId, CommentCreateRequest request);

    void react(Long userId, Long commentId, CommentReactionRequest request);

    void hide(Long userId, Long commentId);

    void unhide(Long userId, Long commentId);

    List<Long> hiddenIds(Long userId, Long chapterId);

    IPage<AdminCommentVO> adminList(long page, long size);

    void adminDelete(Long commentId);
}
