package com.bookstore.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentNoticeVO {
    private Long id;
    private Long commentId;
    private Long bookId;
    private String bookTitle;
    private Long chapterId;
    private String chapterTitle;
    private Integer paragraphIndex;
    private Integer type;
    private Long fromUserId;
    private Integer readFlag;
    private LocalDateTime createTime;
}
