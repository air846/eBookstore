package com.bookstore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookstore.dto.BookQueryRequest;
import com.bookstore.dto.BookSaveRequest;
import com.bookstore.dto.ChapterSaveRequest;
import com.bookstore.dto.HistorySaveRequest;
import com.bookstore.entity.Book;
import com.bookstore.entity.BookChapter;
import com.bookstore.entity.ReadHistory;
import com.bookstore.vo.FileUploadVO;
import com.bookstore.vo.PreferenceStatVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {

    IPage<Book> pageBooks(BookQueryRequest request, boolean adminMode);

    Book detail(Long id);

    String readBook(Long userId, String ip, Long bookId);

    String readTxtContent(Long bookId);

    Integer importTxtChapters(Long bookId);

    FileUploadVO uploadBookFile(MultipartFile file);

    void favorite(Long userId, Long bookId);

    void unfavorite(Long userId, Long bookId);

    List<Book> favoriteList(Long userId);

    void saveHistory(Long userId, HistorySaveRequest request);

    List<ReadHistory> historyList(Long userId);

    List<PreferenceStatVO> preferenceStats(Long userId);

    void create(BookSaveRequest request);

    void update(Long id, BookSaveRequest request);

    void updateStatus(Long id, Integer status);

    void delete(Long id);

    List<BookChapter> listChapters(Long bookId);

    void createChapter(Long bookId, ChapterSaveRequest request);

    void updateChapter(Long bookId, Long chapterId, ChapterSaveRequest request);

    void deleteChapter(Long bookId, Long chapterId);
}
