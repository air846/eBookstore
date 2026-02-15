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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Override
    public void exportReport(OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // 创建样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // 1. 概览统计
        Sheet summarySheet = workbook.createSheet("概览统计");
        createSummarySheet(summarySheet, headerStyle);

        // 2. 用户列表
        Sheet userSheet = workbook.createSheet("用户列表");
        createUserSheet(userSheet, headerStyle);

        // 3. 书籍列表
        Sheet bookSheet = workbook.createSheet("书籍列表");
        createBookSheet(bookSheet, headerStyle);

        // 4. 分类统计
        Sheet categorySheet = workbook.createSheet("分类统计");
        createCategorySheet(categorySheet, headerStyle);

        workbook.write(outputStream);
        workbook.close();
    }

    private void createSummarySheet(Sheet sheet, CellStyle headerStyle) {
        String[] headers = {"统计项", "数值"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        DashboardStatsVO stats = dashboard();
        String[][] data = {
            {"总用户数", String.valueOf(stats.getTotalUsers())},
            {"总书籍数", String.valueOf(stats.getTotalBooks())},
            {"今日访问量", String.valueOf(stats.getTodayVisits())},
            {"导出时间", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}
        };

        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < data[i].length; j++) {
                row.createCell(j).setCellValue(data[i][j]);
            }
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createUserSheet(Sheet sheet, CellStyle headerStyle) {
        String[] headers = {"ID", "用户名", "昵称", "邮箱", "状态", "注册时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().orderByDesc(User::getCreateTime));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getNickname() != null ? user.getNickname() : "");
            row.createCell(3).setCellValue(user.getEmail() != null ? user.getEmail() : "");
            row.createCell(4).setCellValue(user.getStatus() == 1 ? "正常" : "禁用");
            row.createCell(5).setCellValue(user.getCreateTime() != null ? user.getCreateTime().format(formatter) : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createBookSheet(Sheet sheet, CellStyle headerStyle) {
        String[] headers = {"ID", "书名", "作者", "分类", "状态", "访问量", "创建时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        List<Book> books = bookMapper.selectList(new LambdaQueryWrapper<Book>().orderByDesc(Book::getCreateTime));
        List<Category> categories = categoryMapper.selectList(null);
        Map<Long, String> categoryMap = categories.stream()
            .collect(Collectors.toMap(Category::getId, Category::getName));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(book.getId());
            row.createCell(1).setCellValue(book.getTitle());
            row.createCell(2).setCellValue(book.getAuthor() != null ? book.getAuthor() : "");
            row.createCell(3).setCellValue(book.getCategoryId() != null ? categoryMap.getOrDefault(book.getCategoryId(), "未分类") : "未分类");
            row.createCell(4).setCellValue(book.getStatus() == 1 ? "上架" : "下架");
            row.createCell(5).setCellValue(book.getVisitCount() != null ? book.getVisitCount() : 0);
            row.createCell(6).setCellValue(book.getCreateTime() != null ? book.getCreateTime().format(formatter) : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createCategorySheet(Sheet sheet, CellStyle headerStyle) {
        String[] headers = {"分类名称", "书籍数量"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        List<PreferenceStatVO> stats = buildCategoryBookStats();

        for (int i = 0; i < stats.size(); i++) {
            PreferenceStatVO stat = stats.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(stat.getName());
            row.createCell(1).setCellValue(stat.getValue());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
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
