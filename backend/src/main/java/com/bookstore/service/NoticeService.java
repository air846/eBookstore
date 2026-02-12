package com.bookstore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookstore.vo.CommentNoticeVO;

public interface NoticeService {

    IPage<CommentNoticeVO> list(Long userId, long page, long size);

    void markRead(Long userId, Long noticeId);

    void markAllRead(Long userId);
}
