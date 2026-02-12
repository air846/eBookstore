package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.common.BizException;
import com.bookstore.entity.Book;
import com.bookstore.entity.User;
import com.bookstore.entity.VisitLog;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.UserMapper;
import com.bookstore.mapper.VisitLogMapper;
import com.bookstore.service.AdminService;
import com.bookstore.vo.DashboardStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final VisitLogMapper visitLogMapper;

    @Override
    public DashboardStatsVO dashboard() {
        DashboardStatsVO vo = new DashboardStatsVO();
        vo.setTotalUsers(userMapper.selectCount(null));
        vo.setTotalBooks(bookMapper.selectCount(null));
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        vo.setTodayVisits(visitLogMapper.selectCount(new LambdaQueryWrapper<VisitLog>().ge(VisitLog::getVisitTime, todayStart)));
        List<Book> hotBooks = bookMapper.selectList(new LambdaQueryWrapper<Book>()
                .orderByDesc(Book::getVisitCount)
                .last("limit 8"));
        vo.setHotBooks(hotBooks);
        return vo;
    }

    @Override
    public IPage<User> pageUsers(long page, long size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword).or().like(User::getNickname, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);
        IPage<User> result = userMapper.selectPage(new Page<>(page, size), wrapper);
        result.getRecords().forEach(item -> item.setPassword(null));
        return result;
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }
}
