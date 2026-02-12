package com.bookstore.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminCommentVO {
    private Long id;
    private Long bookId;
    private Long chapterId;
    private Integer paragraphIndex;
    private Long userId;
    private String content;
    private Integer status;
    private Integer likeCount;
    private Integer dislikeCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
