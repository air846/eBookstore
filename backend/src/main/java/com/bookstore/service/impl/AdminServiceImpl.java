package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.common.BizException;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.entity.User;
import com.bookstore.entity.VisitLog;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.mapper.UserMapper;
import com.bookstore.mapper.VisitLogMapper;
import com.bookstore.service.AdminService;
import com.bookstore.vo.DailyVisitPointVO;
import com.bookstore.vo.DashboardStatsVO;
import com.bookstore.vo.PreferenceStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// 管理端业务实现：概览与用户/书籍管理
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final CategoryMapper categoryMapper;
    private final VisitLogMapper visitLogMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
        vo.setDailyVisitTrend(buildDailyVisitTrend(7));
        vo.setCategoryBookStats(buildCategoryBookStats());
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

    @Override
    public void resetUserPassword(Long id, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        user.setPassword(encoder.encode(newPassword));
        userMapper.updateById(user);
    }

    private List<DailyVisitPointVO> buildDailyVisitTrend(int days) {
        List<DailyVisitPointVO> result = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            LocalDateTime start = day.atStartOfDay();
            LocalDateTime end = day.plusDays(1).atStartOfDay();
            Long count = visitLogMapper.selectCount(new LambdaQueryWrapper<VisitLog>()
                    .ge(VisitLog::getVisitTime, start)
                    .lt(VisitLog::getVisitTime, end));
            result.add(new DailyVisitPointVO(day.toString(), count == null ? 0L : count));
        }
        return result;
    }

    private List<PreferenceStatVO> buildCategoryBookStats() {
        List<Book> books = bookMapper.selectList(new LambdaQueryWrapper<Book>()
                .select(Book::getCategoryId));
        if (books.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> countMap = new HashMap<>();
        for (Book book : books) {
            if (book.getCategoryId() == null) {
                continue;
            }
            countMap.merge(book.getCategoryId(), 1L, Long::sum);
        }
        if (countMap.isEmpty()) {
            return List.of();
        }

        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .select(Category::getId, Category::getName));
        Map<Long, String> nameMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        return countMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(item -> new PreferenceStatVO(nameMap.getOrDefault(item.getKey(), "其他"), item.getValue()))
                .collect(Collectors.toList());
    }
}
