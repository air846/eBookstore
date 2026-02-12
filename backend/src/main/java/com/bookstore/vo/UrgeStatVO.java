package com.bookstore.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrgeStatVO {
    private Long bookId;
    private String bookTitle;
    private Long count;
    private LocalDateTime latestTime;
}
