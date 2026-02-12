package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.entity.Book;
import com.bookstore.entity.BookChapter;
import com.bookstore.entity.CommentNotice;
import com.bookstore.mapper.BookChapterMapper;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.CommentNoticeMapper;
import com.bookstore.service.NoticeService;
import com.bookstore.vo.CommentNoticeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final CommentNoticeMapper commentNoticeMapper;
    private final BookMapper bookMapper;
    private final BookChapterMapper bookChapterMapper;

    @Override
    public IPage<CommentNoticeVO> list(Long userId, long page, long size) {
        IPage<CommentNotice> result = commentNoticeMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<CommentNotice>()
                        .eq(CommentNotice::getUserId, userId)
                        .orderByDesc(CommentNotice::getCreateTime));

        Map<Long, String> bookTitleMap = new HashMap<>();
        Map<Long, String> chapterTitleMap = new HashMap<>();
        if (!result.getRecords().isEmpty()) {
            Set<Long> bookIds = result.getRecords().stream().map(CommentNotice::getBookId).collect(Collectors.toSet());
            Set<Long> chapterIds = result.getRecords().stream().map(CommentNotice::getChapterId).collect(Collectors.toSet());
            if (!bookIds.isEmpty()) {
                List<Book> books = bookMapper.selectList(new LambdaQueryWrapper<Book>().in(Book::getId, bookIds));
                for (Book book : books) {
                    bookTitleMap.put(book.getId(), book.getTitle());
                }
            }
            if (!chapterIds.isEmpty()) {
                List<BookChapter> chapters = bookChapterMapper.selectList(new LambdaQueryWrapper<BookChapter>().in(BookChapter::getId, chapterIds));
                for (BookChapter chapter : chapters) {
                    chapterTitleMap.put(chapter.getId(), chapter.getTitle());
                }
            }
        }

        IPage<CommentNoticeVO> pageResult = new Page<>(page, size, result.getTotal());
        List<CommentNoticeVO> items = result.getRecords().stream().map(item -> {
            CommentNoticeVO vo = new CommentNoticeVO();
            vo.setId(item.getId());
            vo.setCommentId(item.getCommentId());
            vo.setBookId(item.getBookId());
            vo.setBookTitle(bookTitleMap.get(item.getBookId()));
            vo.setChapterId(item.getChapterId());
            vo.setChapterTitle(chapterTitleMap.get(item.getChapterId()));
            vo.setParagraphIndex(item.getParagraphIndex());
            vo.setType(item.getType());
            vo.setFromUserId(item.getFromUserId());
            vo.setReadFlag(item.getReadFlag());
            vo.setCreateTime(item.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
        pageResult.setRecords(items);
        return pageResult;
    }

    @Override
    public void markRead(Long userId, Long noticeId) {
        CommentNotice notice = commentNoticeMapper.selectById(noticeId);
        if (notice == null || !notice.getUserId().equals(userId)) {
            return;
        }
        notice.setReadFlag(1);
        commentNoticeMapper.updateById(notice);
    }

    @Override
    public void markAllRead(Long userId) {
        List<CommentNotice> notices = commentNoticeMapper.selectList(new LambdaQueryWrapper<CommentNotice>()
                .eq(CommentNotice::getUserId, userId)
                .eq(CommentNotice::getReadFlag, 0));
        for (CommentNotice notice : notices) {
            notice.setReadFlag(1);
            commentNoticeMapper.updateById(notice);
        }
    }
}
