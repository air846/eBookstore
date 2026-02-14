package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.common.BizException;
import com.bookstore.dto.BookQueryRequest;
import com.bookstore.dto.BookSaveRequest;
import com.bookstore.dto.ChapterSaveRequest;
import com.bookstore.dto.HistorySaveRequest;
import com.bookstore.entity.Book;
import com.bookstore.entity.BookChapter;
import com.bookstore.entity.Category;
import com.bookstore.entity.Favorite;
import com.bookstore.entity.ReadHistory;
import com.bookstore.entity.VisitLog;
import com.bookstore.mapper.BookChapterMapper;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.mapper.FavoriteMapper;
import com.bookstore.mapper.ReadHistoryMapper;
import com.bookstore.mapper.VisitLogMapper;
import com.bookstore.service.BookService;
import com.bookstore.vo.PreferenceStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// 书籍业务实现：查询、章节、收藏与阅读记录
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BookChapterMapper bookChapterMapper;
    private final CategoryMapper categoryMapper;
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
            wrapper.in(Book::getCategoryId, resolveCategoryIds(request.getCategoryId()));
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
        validateBookHasResource(book.getId(), book.getFileUrl());
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
    public String readTxtContent(Long bookId) {
        Book book = detail(bookId);
        String fileUrl = book.getFileUrl();
        if (!StringUtils.hasText(fileUrl)) {
            throw new BizException(400, "文件地址为空");
        }
        String normalizedPath = normalizePath(fileUrl);
        if (!normalizedPath.toLowerCase().endsWith(".txt")) {
            throw new BizException(400, "当前仅支持读取 TXT 文件");
        }
        try {
            Path path = Paths.get(normalizedPath);
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                throw new BizException(404, "文件不存在");
            }
            byte[] bytes = Files.readAllBytes(path);
            String content = new String(bytes, StandardCharsets.UTF_8);
            if (content.contains("\uFFFD")) {
                content = new String(bytes, Charset.forName("GBK"));
            }
            return content;
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException(500, "读取文件失败: " + ex.getMessage());
        }
    }

    @Override
    public Integer importTxtChapters(Long bookId) {
        Book book = detail(bookId);
        String fileUrl = book.getFileUrl();
        if (!StringUtils.hasText(fileUrl)) {
            throw new BizException(400, "文件地址为空");
        }
        if (!"TXT".equalsIgnoreCase(book.getFileType())) {
            throw new BizException(400, "仅支持 TXT 书籍导入章节");
        }
        String content = readTxtContent(bookId);
        List<BookChapter> parsed = parseTxtChapters(content, bookId);
        if (parsed.isEmpty()) {
            throw new BizException(400, "TXT 内容为空，无法导入章节");
        }

        bookChapterMapper.delete(new LambdaQueryWrapper<BookChapter>().eq(BookChapter::getBookId, bookId));
        LocalDateTime now = LocalDateTime.now();
        parsed.forEach(item -> {
            item.setCreateTime(now);
            item.setUpdateTime(now);
            bookChapterMapper.insert(item);
        });
        return parsed.size();
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
    public void unfavorite(Long userId, Long bookId) {
        detail(bookId);
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getBookId, bookId));
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
        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            orderMap.put(ids.get(i), i);
        }
        List<Book> books = bookMapper.selectList(new LambdaQueryWrapper<Book>().in(Book::getId, ids));
        books.sort((a, b) -> Integer.compare(
                orderMap.getOrDefault(a.getId(), Integer.MAX_VALUE),
                orderMap.getOrDefault(b.getId(), Integer.MAX_VALUE)));
        return books;
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
    public List<PreferenceStatVO> preferenceStats(Long userId) {
        List<ReadHistory> histories = readHistoryMapper.selectList(new LambdaQueryWrapper<ReadHistory>()
                .eq(ReadHistory::getUserId, userId));
        if (histories.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> bookIds = histories.stream()
                .map(ReadHistory::getBookId)
                .distinct()
                .collect(Collectors.toList());
        if (bookIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Book> books = bookMapper.selectList(new LambdaQueryWrapper<Book>()
                .select(Book::getId, Book::getCategoryId)
                .in(Book::getId, bookIds));
        if (books.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> categoryCountMap = new HashMap<>();
        for (Book item : books) {
            if (item.getCategoryId() == null) {
                continue;
            }
            categoryCountMap.merge(item.getCategoryId(), 1L, Long::sum);
        }
        if (categoryCountMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .select(Category::getId, Category::getName));
        Map<Long, String> categoryNameMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        return categoryCountMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(item -> new PreferenceStatVO(categoryNameMap.getOrDefault(item.getKey(), "其他"), item.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public void create(BookSaveRequest request) {
        validateBookResourceForSave(null, request);
        Book book = new Book();
        fillBook(book, request);
        book.setVisitCount(0L);
        book.setCreateTime(LocalDateTime.now());
        bookMapper.insert(book);
    }

    @Override
    public void update(Long id, BookSaveRequest request) {
        Book book = detail(id);
        validateBookResourceForSave(id, request);
        fillBook(book, request);
        bookMapper.updateById(book);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Book book = detail(id);
        if (status != null && status == 1) {
            validateBookHasResource(book.getId(), book.getFileUrl());
        }
        book.setStatus(status);
        bookMapper.updateById(book);
    }

    @Override
    public void delete(Long id) {
        detail(id);
        bookChapterMapper.delete(new LambdaQueryWrapper<BookChapter>()
                .eq(BookChapter::getBookId, id));
        bookMapper.deleteById(id);
    }

    @Override
    public List<BookChapter> listChapters(Long bookId) {
        detail(bookId);
        return bookChapterMapper.selectList(new LambdaQueryWrapper<BookChapter>()
                .eq(BookChapter::getBookId, bookId)
                .orderByAsc(BookChapter::getSortOrder)
                .orderByAsc(BookChapter::getId));
    }

    @Override
    public void createChapter(Long bookId, ChapterSaveRequest request) {
        detail(bookId);
        BookChapter chapter = new BookChapter();
        chapter.setBookId(bookId);
        chapter.setTitle(request.getTitle());
        chapter.setContent(request.getContent());
        chapter.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        chapter.setCreateTime(LocalDateTime.now());
        chapter.setUpdateTime(LocalDateTime.now());
        bookChapterMapper.insert(chapter);
    }

    @Override
    public void updateChapter(Long bookId, Long chapterId, ChapterSaveRequest request) {
        detail(bookId);
        BookChapter chapter = chapterDetail(bookId, chapterId);
        chapter.setTitle(request.getTitle());
        chapter.setContent(request.getContent());
        chapter.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        chapter.setUpdateTime(LocalDateTime.now());
        bookChapterMapper.updateById(chapter);
    }

    @Override
    public void deleteChapter(Long bookId, Long chapterId) {
        detail(bookId);
        chapterDetail(bookId, chapterId);
        bookChapterMapper.deleteById(chapterId);
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
        book.setStatus(request.getStatus() == null ? 0 : request.getStatus());
    }

    private void validateBookResourceForSave(Long bookId, BookSaveRequest request) {
        Integer status = request.getStatus() == null ? 0 : request.getStatus();
        if (status != 1) {
            return;
        }
        boolean hasFile = StringUtils.hasText(request.getFileUrl());
        boolean hasChapter = bookId != null && hasChapterContent(bookId);
        if (!hasFile && !hasChapter) {
            throw new BizException(400, "上架书籍至少需要文件地址或章节内容");
        }
    }

    private void validateBookHasResource(Long bookId, String fileUrl) {
        if (StringUtils.hasText(fileUrl) || hasChapterContent(bookId)) {
            return;
        }
        throw new BizException(400, "该书暂无可阅读内容");
    }

    private boolean hasChapterContent(Long bookId) {
        return bookChapterMapper.selectCount(new LambdaQueryWrapper<BookChapter>()
                .eq(BookChapter::getBookId, bookId)
                .isNotNull(BookChapter::getContent)
                .ne(BookChapter::getContent, "")) > 0;
    }

    private List<Long> resolveCategoryIds(Long rootCategoryId) {
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .select(Category::getId, Category::getParentId));

        Map<Long, List<Long>> childrenMap = new HashMap<>();
        for (Category category : categories) {
            Long parentId = category.getParentId();
            if (parentId == null || parentId == 0) {
                continue;
            }
            childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(category.getId());
        }

        Set<Long> result = new LinkedHashSet<>();
        Deque<Long> queue = new ArrayDeque<>();
        queue.add(rootCategoryId);
        while (!queue.isEmpty()) {
            Long currentId = queue.poll();
            if (!result.add(currentId)) {
                continue;
            }
            List<Long> children = childrenMap.getOrDefault(currentId, Collections.emptyList());
            queue.addAll(children);
        }
        return new ArrayList<>(result);
    }

    private BookChapter chapterDetail(Long bookId, Long chapterId) {
        BookChapter chapter = bookChapterMapper.selectOne(new LambdaQueryWrapper<BookChapter>()
                .eq(BookChapter::getId, chapterId)
                .eq(BookChapter::getBookId, bookId)
                .last("limit 1"));
        if (chapter == null) {
            throw new BizException(404, "chapter not found");
        }
        return chapter;
    }

    private String normalizePath(String path) {
        String result = path.trim();
        if (result.length() >= 2 && result.startsWith("\"") && result.endsWith("\"")) {
            result = result.substring(1, result.length() - 1).trim();
        }
        return result;
    }

    private List<BookChapter> parseTxtChapters(String rawContent, Long bookId) {
        String content = rawContent == null ? "" : rawContent.replace("\r\n", "\n").trim();
        if (content.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> lines = List.of(content.split("\n", -1));
        Pattern titlePattern = Pattern.compile("^(第[0-9一二三四五六七八九十百千万零两]+[章节卷篇部回].*|(?:chapter|chap\\.?)\\s*\\d+.*)$", Pattern.CASE_INSENSITIVE);
        List<Integer> titleIndexes = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String text = lines.get(i).trim();
            if (text.isEmpty() || text.length() > 60) {
                continue;
            }
            if (titlePattern.matcher(text).matches()) {
                titleIndexes.add(i);
            }
        }

        List<BookChapter> chapters = new ArrayList<>();
        if (!titleIndexes.isEmpty()) {
            int firstTitleIndex = titleIndexes.get(0);
            String preface = String.join("\n", lines.subList(0, firstTitleIndex)).trim();
            if (StringUtils.hasText(preface)) {
                chapters.add(newTxtChapter(bookId, "前言", preface, chapters.size() + 1));
            }
            for (int i = 0; i < titleIndexes.size(); i++) {
                int start = titleIndexes.get(i);
                int end = i == titleIndexes.size() - 1 ? lines.size() : titleIndexes.get(i + 1);
                String title = lines.get(start).trim();
                String body = String.join("\n", lines.subList(start + 1, end)).trim();
                if (!StringUtils.hasText(body)) {
                    continue;
                }
                chapters.add(newTxtChapter(bookId, StringUtils.hasText(title) ? title : ("第" + (i + 1) + "章"), body, chapters.size() + 1));
            }
            return chapters;
        }

        int maxChars = 8000;
        String[] paragraphs = content.split("\\n{2,}");
        StringBuilder buffer = new StringBuilder();
        int partIndex = 1;
        for (String paragraph : paragraphs) {
            String item = paragraph.trim();
            if (!StringUtils.hasText(item)) {
                continue;
            }
            String candidate = buffer.length() == 0 ? item : buffer + "\n\n" + item;
            if (candidate.length() > maxChars && buffer.length() > 0) {
                chapters.add(newTxtChapter(bookId, "第" + partIndex + "节", buffer.toString().trim(), chapters.size() + 1));
                partIndex++;
                buffer = new StringBuilder(item);
            } else {
                buffer = new StringBuilder(candidate);
            }
        }
        if (buffer.length() > 0) {
            chapters.add(newTxtChapter(bookId, "第" + partIndex + "节", buffer.toString().trim(), chapters.size() + 1));
        }
        return chapters;
    }

    private BookChapter newTxtChapter(Long bookId, String title, String content, int sortOrder) {
        BookChapter chapter = new BookChapter();
        chapter.setBookId(bookId);
        chapter.setTitle(title);
        chapter.setContent(content);
        chapter.setSortOrder(sortOrder);
        return chapter;
    }
}
