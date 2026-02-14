package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.BizException;
import com.bookstore.entity.BookUrge;
import com.bookstore.mapper.BookUrgeMapper;
import com.bookstore.service.UrgeService;
import com.bookstore.vo.UrgeStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrgeServiceImpl implements UrgeService {

    private final BookUrgeMapper bookUrgeMapper;

    @Override
    public void create(Long userId, Long bookId) {
        BookUrge exists = bookUrgeMapper.selectOne(new LambdaQueryWrapper<BookUrge>()
                .eq(BookUrge::getUserId, userId)
                .eq(BookUrge::getBookId, bookId)
                .last("limit 1"));
        if (exists != null) {
            throw new BizException("你已经催更过这本书了");
        }
        BookUrge urge = new BookUrge();
        urge.setBookId(bookId);
        urge.setUserId(userId);
        urge.setCreateTime(LocalDateTime.now());
        bookUrgeMapper.insert(urge);
    }

    @Override
    public List<UrgeStatVO> listStats() {
        return bookUrgeMapper.listStats();
    }
}
