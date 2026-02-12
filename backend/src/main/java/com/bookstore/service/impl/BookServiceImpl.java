package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.common.BizException;
import com.bookstore.dto.BookQueryRequest;
import com.bookstore.dto.BookSaveRequest;
import com.bookstore.dto.HistorySaveRequest;
import com.bookstore.entity.Book;
import com.bookstore.entity.Favorite;
import com.bookstore.entity.ReadHistory;
import com.bookstore.entity.VisitLog;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.FavoriteMapper;
import com.bookstore.mapper.ReadHistoryMapper;
import com.bookstore.mapper.VisitLogMapper;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final FavoriteMapper favoriteMapper;
    private final ReadHistoryMapper readHistoryMapper;
    private final VisitLogMapper visitLogMapper;

    @Override
    public IPage<Book> pageBooks(BookQueryRequest request, boolean adminMode) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        if (!adminMode) {
            wrapper.eq(Book::getStatus, 1);
        }
        if (request.getCategoryId() != null) {
            wrapper.eq(Book::getCategoryId, request.getCategoryId());
        }
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w.like(Book::getTitle, request.getKeyword()).or().like(Book::getAuthor, request.getKeyword()));
        }
        if (StringUtils.hasText(request.getAuthor())) {
            wrapper.like(Book::getAuthor, request.getAuthor());
        }
        boolean asc = "asc".equalsIgnoreCase(request.getOrder());
        if ("visit_count".equalsIgnoreCase(request.getSortBy())) {
            wrapper.orderBy(true, asc, Book::getVisitCount);
        } else {
            wrapper.orderBy(true, asc, Book::getCreateTime);
        }
        return bookMapper.selectPage(new Page<>(request.getPage(), request.getSize()), wrapper);
    }

    @Override
    public Book detail(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BizException(404, "书籍不存在");
        }
        return book;
    }

    @Override
    public String readBook(Long userId, String ip, Long bookId) {
        Book book = detail(bookId);
        long currentCount = book.getVisitCount() == null ? 0L : book.getVisitCount();
        book.setVisitCount(currentCount + 1);
        bookMapper.updateById(book);

        VisitLog log = new VisitLog();
        log.setUserId(userId);
        log.setIp(ip);
        log.setUrl("/api/book/read/" + bookId);
        log.setVisitTime(LocalDateTime.now());
        visitLogMapper.insert(log);
        return book.getFileUrl();
    }

    @Override
    public void favorite(Long userId, Long bookId) {
        detail(bookId);
        Favorite exists = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getBookId, bookId)
                .last("limit 1"));
        if (exists != null) {
            return;
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);
    }

    @Override
    public List<Book> favoriteList(Long userId) {
        List<Favorite> favorites = favoriteMapper.selectList(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime));
        if (favorites.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = favorites.stream().map(Favorite::getBookId).collect(Collectors.toList());
        return bookMapper.selectList(new LambdaQueryWrapper<Book>().in(Book::getId, ids));
    }

    @Override
    public void saveHistory(Long userId, HistorySaveRequest request) {
        detail(request.getBookId());
        ReadHistory history = readHistoryMapper.selectOne(new LambdaQueryWrapper<ReadHistory>()
                .eq(ReadHistory::getUserId, userId)
                .eq(ReadHistory::getBookId, request.getBookId())
                .last("limit 1"));
        if (history == null) {
            history = new ReadHistory();
            history.setUserId(userId);
            history.setBookId(request.getBookId());
        }
        history.setChapter(request.getChapter());
        history.setProgress(request.getProgress());
        history.setReadTime(LocalDateTime.now());
        if (history.getId() == null) {
            readHistoryMapper.insert(history);
        } else {
            readHistoryMapper.updateById(history);
        }
    }

    @Override
    public List<ReadHistory> historyList(Long userId) {
        return readHistoryMapper.selectList(new LambdaQueryWrapper<ReadHistory>()
                .eq(ReadHistory::getUserId, userId)
                .orderByDesc(ReadHistory::getReadTime));
    }

    @Override
    public void create(BookSaveRequest request) {
        Book book = new Book();
        fillBook(book, request);
        book.setVisitCount(0L);
        book.setCreateTime(LocalDateTime.now());
        bookMapper.insert(book);
    }

    @Override
    public void update(Long id, BookSaveRequest request) {
        Book book = detail(id);
        fillBook(book, request);
        bookMapper.updateById(book);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Book book = detail(id);
        book.setStatus(status);
        bookMapper.updateById(book);
    }

    @Override
    public void delete(Long id) {
        bookMapper.deleteById(id);
    }

    private void fillBook(Book book, BookSaveRequest request) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setIsbn(request.getIsbn());
        book.setCategoryId(request.getCategoryId());
        book.setCoverUrl(request.getCoverUrl());
        book.setDescription(request.getDescription());
        book.setFileUrl(request.getFileUrl());
        book.setFileType(request.getFileType());
        book.setStatus(request.getStatus() == null ? 1 : request.getStatus());
    }
}
