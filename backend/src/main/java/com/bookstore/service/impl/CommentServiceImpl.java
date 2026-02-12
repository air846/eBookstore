package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.common.BizException;
import com.bookstore.dto.CommentCreateRequest;
import com.bookstore.dto.CommentReactionRequest;
import com.bookstore.entity.BookComment;
import com.bookstore.entity.CommentNotice;
import com.bookstore.entity.CommentHide;
import com.bookstore.entity.CommentReaction;
import com.bookstore.mapper.BookCommentMapper;
import com.bookstore.mapper.CommentHideMapper;
import com.bookstore.mapper.CommentNoticeMapper;
import com.bookstore.mapper.CommentReactionMapper;
import com.bookstore.service.CommentService;
import com.bookstore.vo.AdminCommentVO;
import com.bookstore.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// 段评与互动业务实现
public class CommentServiceImpl implements CommentService {

    private static final int STATUS_APPROVED = 1;

    private final BookCommentMapper bookCommentMapper;
    private final CommentReactionMapper commentReactionMapper;
    private final CommentHideMapper commentHideMapper;
    private final CommentNoticeMapper commentNoticeMapper;

    @Override
    public List<CommentVO> listComments(Long userId, Long bookId, Long chapterId, Integer paragraphIndex, String sortBy) {
        if (chapterId == null || paragraphIndex == null) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<BookComment> wrapper = new LambdaQueryWrapper<BookComment>()
                .eq(BookComment::getBookId, bookId)
                .eq(BookComment::getChapterId, chapterId)
                .eq(BookComment::getParagraphIndex, paragraphIndex)
                .eq(BookComment::getStatus, STATUS_APPROVED)
                .eq(BookComment::getDeleted, 0)
                .isNull(BookComment::getParentId);
        if ("hot".equalsIgnoreCase(sortBy)) {
            wrapper.last("ORDER BY (like_count - dislike_count) / (TIMESTAMPDIFF(HOUR, create_time, NOW()) + 2) DESC, create_time DESC");
        } else {
            wrapper.orderByDesc(BookComment::getCreateTime);
        }
        List<BookComment> comments = bookCommentMapper.selectList(wrapper);

        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> rootIds = comments.stream().map(BookComment::getId).collect(Collectors.toList());
        List<BookComment> replyComments = bookCommentMapper.selectList(new LambdaQueryWrapper<BookComment>()
                .eq(BookComment::getBookId, bookId)
                .eq(BookComment::getChapterId, chapterId)
                .eq(BookComment::getParagraphIndex, paragraphIndex)
                .eq(BookComment::getStatus, STATUS_APPROVED)
                .eq(BookComment::getDeleted, 0)
                .in(BookComment::getParentId, rootIds)
                .orderByAsc(BookComment::getCreateTime));
        List<Long> commentIds = new ArrayList<>(rootIds);
        replyComments.forEach(item -> commentIds.add(item.getId()));
        Map<Long, Integer> reactionMap = new HashMap<>();
        Map<Long, Boolean> hiddenMap = new HashMap<>();

        if (userId != null) {
            List<CommentReaction> reactions = commentReactionMapper.selectList(new LambdaQueryWrapper<CommentReaction>()
                    .eq(CommentReaction::getUserId, userId)
                    .in(CommentReaction::getCommentId, commentIds));
            reactions.forEach(item -> reactionMap.put(item.getCommentId(), item.getValue()));

            List<CommentHide> hides = commentHideMapper.selectList(new LambdaQueryWrapper<CommentHide>()
                    .eq(CommentHide::getUserId, userId)
                    .in(CommentHide::getCommentId, commentIds));
            hides.forEach(item -> hiddenMap.put(item.getCommentId(), true));
        }

        Map<Long, List<CommentVO>> replyMap = new HashMap<>();
        for (BookComment reply : replyComments) {
            CommentVO vo = new CommentVO();
            vo.setId(reply.getId());
            vo.setBookId(reply.getBookId());
            vo.setChapterId(reply.getChapterId());
            vo.setParagraphIndex(reply.getParagraphIndex());
            vo.setUserId(reply.getUserId());
            vo.setParentId(reply.getParentId());
            vo.setContent(reply.getContent());
            vo.setLikeCount(reply.getLikeCount());
            vo.setDislikeCount(reply.getDislikeCount());
            vo.setUserReaction(reactionMap.getOrDefault(reply.getId(), 0));
            vo.setHidden(hiddenMap.getOrDefault(reply.getId(), false));
            vo.setCreateTime(reply.getCreateTime());
            replyMap.computeIfAbsent(reply.getParentId(), key -> new ArrayList<>()).add(vo);
        }

        List<CommentVO> result = new ArrayList<>();
        for (BookComment comment : comments) {
            CommentVO vo = new CommentVO();
            vo.setId(comment.getId());
            vo.setBookId(comment.getBookId());
            vo.setChapterId(comment.getChapterId());
            vo.setParagraphIndex(comment.getParagraphIndex());
            vo.setUserId(comment.getUserId());
            vo.setParentId(comment.getParentId());
            vo.setContent(comment.getContent());
            vo.setLikeCount(comment.getLikeCount());
            vo.setDislikeCount(comment.getDislikeCount());
            vo.setUserReaction(reactionMap.getOrDefault(comment.getId(), 0));
            vo.setHidden(hiddenMap.getOrDefault(comment.getId(), false));
            vo.setCreateTime(comment.getCreateTime());
            vo.setReplies(replyMap.getOrDefault(comment.getId(), new ArrayList<>()));
            result.add(vo);
        }
        return result;
    }

    @Override
    public void createComment(Long userId, Long bookId, Long chapterId, CommentCreateRequest request) {
        if (!StringUtils.hasText(request.getContent())) {
            throw new BizException(400, "评论内容不能为空");
        }
        Long parentId = request.getParentId();
        if (parentId != null) {
            BookComment parent = bookCommentMapper.selectById(parentId);
            if (parent == null || parent.getDeleted() != 0 || parent.getStatus() != STATUS_APPROVED) {
                throw new BizException(404, "父评论不存在");
            }
            if (parent.getParentId() != null) {
                throw new BizException(400, "仅支持回复一级评论");
            }
            if (!parent.getBookId().equals(bookId)
                    || !parent.getChapterId().equals(chapterId)
                    || !parent.getParagraphIndex().equals(request.getParagraphIndex())) {
                throw new BizException(400, "父评论与段落不匹配");
            }
        }
        BookComment comment = new BookComment();
        comment.setBookId(bookId);
        comment.setChapterId(chapterId);
        comment.setParagraphIndex(request.getParagraphIndex());
        comment.setUserId(userId);
        comment.setParentId(parentId);
        comment.setContent(request.getContent().trim());
        comment.setStatus(STATUS_APPROVED);
        comment.setLikeCount(0);
        comment.setDislikeCount(0);
        comment.setDeleted(0);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        bookCommentMapper.insert(comment);
    }

    @Override
    @Transactional
    public void react(Long userId, Long commentId, CommentReactionRequest request) {
        int value = request.getValue();
        if (value != -1 && value != 0 && value != 1) {
            throw new BizException(400, "reaction value invalid");
        }
        BookComment comment = bookCommentMapper.selectById(commentId);
        if (comment == null || comment.getDeleted() != 0 || comment.getStatus() != STATUS_APPROVED) {
            throw new BizException(404, "comment not found");
        }

        CommentReaction existing = commentReactionMapper.selectOne(new LambdaQueryWrapper<CommentReaction>()
                .eq(CommentReaction::getUserId, userId)
                .eq(CommentReaction::getCommentId, commentId)
                .last("limit 1"));

        int prev = existing == null ? 0 : existing.getValue();
        if (prev == value) {
            return;
        }

        if (value == 0) {
            if (existing != null) {
                commentReactionMapper.deleteById(existing.getId());
            }
        } else if (existing == null) {
            CommentReaction reaction = new CommentReaction();
            reaction.setCommentId(commentId);
            reaction.setUserId(userId);
            reaction.setValue(value);
            reaction.setCreateTime(LocalDateTime.now());
            reaction.setUpdateTime(LocalDateTime.now());
            commentReactionMapper.insert(reaction);
        } else {
            existing.setValue(value);
            existing.setUpdateTime(LocalDateTime.now());
            commentReactionMapper.updateById(existing);
        }

        int likeCount = comment.getLikeCount() == null ? 0 : comment.getLikeCount();
        int dislikeCount = comment.getDislikeCount() == null ? 0 : comment.getDislikeCount();
        if (prev == 1) {
            likeCount = Math.max(0, likeCount - 1);
        } else if (prev == -1) {
            dislikeCount = Math.max(0, dislikeCount - 1);
        }
        if (value == 1) {
            likeCount += 1;
        } else if (value == -1) {
            dislikeCount += 1;
        }
        comment.setLikeCount(likeCount);
        comment.setDislikeCount(dislikeCount);
        comment.setUpdateTime(LocalDateTime.now());
        bookCommentMapper.updateById(comment);

        if (value != 0 && !comment.getUserId().equals(userId)) {
            CommentNotice notice = new CommentNotice();
            notice.setUserId(comment.getUserId());
            notice.setCommentId(commentId);
            notice.setBookId(comment.getBookId());
            notice.setChapterId(comment.getChapterId());
            notice.setParagraphIndex(comment.getParagraphIndex());
            notice.setType(value == 1 ? 1 : 2);
            notice.setFromUserId(userId);
            notice.setReadFlag(0);
            notice.setCreateTime(LocalDateTime.now());
            commentNoticeMapper.insert(notice);
        }
    }

    @Override
    public void hide(Long userId, Long commentId) {
        CommentHide exists = commentHideMapper.selectOne(new LambdaQueryWrapper<CommentHide>()
                .eq(CommentHide::getUserId, userId)
                .eq(CommentHide::getCommentId, commentId)
                .last("limit 1"));
        if (exists != null) {
            return;
        }
        CommentHide hide = new CommentHide();
        hide.setUserId(userId);
        hide.setCommentId(commentId);
        hide.setCreateTime(LocalDateTime.now());
        commentHideMapper.insert(hide);
    }

    @Override
    public void unhide(Long userId, Long commentId) {
        commentHideMapper.delete(new LambdaQueryWrapper<CommentHide>()
                .eq(CommentHide::getUserId, userId)
                .eq(CommentHide::getCommentId, commentId));
    }

    @Override
    public List<Long> hiddenIds(Long userId, Long chapterId) {
        if (userId == null || chapterId == null) {
            return new ArrayList<>();
        }
        List<CommentHide> hides = commentHideMapper.selectList(new LambdaQueryWrapper<CommentHide>()
                .eq(CommentHide::getUserId, userId)
                .inSql(CommentHide::getCommentId,
                        "SELECT id FROM book_comment WHERE chapter_id = " + chapterId));
        return hides.stream().map(CommentHide::getCommentId).collect(Collectors.toList());
    }

    @Override
    public IPage<AdminCommentVO> adminList(long page, long size) {
        LambdaQueryWrapper<BookComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookComment::getDeleted, 0);
        wrapper.orderByDesc(BookComment::getCreateTime);
        IPage<BookComment> result = bookCommentMapper.selectPage(new Page<>(page, size), wrapper);
        IPage<AdminCommentVO> pageResult = new Page<>(page, size, result.getTotal());
        List<AdminCommentVO> items = result.getRecords().stream().map(item -> {
            AdminCommentVO vo = new AdminCommentVO();
            vo.setId(item.getId());
            vo.setBookId(item.getBookId());
            vo.setChapterId(item.getChapterId());
            vo.setParagraphIndex(item.getParagraphIndex());
            vo.setUserId(item.getUserId());
            vo.setContent(item.getContent());
            vo.setStatus(item.getStatus());
            vo.setLikeCount(item.getLikeCount());
            vo.setDislikeCount(item.getDislikeCount());
            vo.setCreateTime(item.getCreateTime());
            vo.setUpdateTime(item.getUpdateTime());
            return vo;
        }).collect(Collectors.toList());
        pageResult.setRecords(items);
        return pageResult;
    }

    @Override
    public void adminDelete(Long commentId) {
        BookComment comment = bookCommentMapper.selectById(commentId);
        if (comment == null) {
            return;
        }
        comment.setDeleted(1);
        comment.setUpdateTime(LocalDateTime.now());
        bookCommentMapper.updateById(comment);
    }

    @Override
    public Map<Integer, Long> commentCounts(Long bookId, Long chapterId) {
        if (bookId == null || chapterId == null) {
            return new HashMap<>();
        }
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BookComment> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.select("paragraph_index AS paragraphIndex", "COUNT(*) AS cnt")
                .eq("book_id", bookId)
                .eq("chapter_id", chapterId)
                .eq("status", STATUS_APPROVED)
                .eq("deleted", 0)
                .groupBy("paragraph_index");
        List<Map<String, Object>> rows = bookCommentMapper.selectMaps(wrapper);
        Map<Integer, Long> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Object idx = row.get("paragraphIndex");
            Object cnt = row.get("cnt");
            if (idx != null && cnt != null) {
                result.put(((Number) idx).intValue(), ((Number) cnt).longValue());
            }
        }
        return result;
    }
}
