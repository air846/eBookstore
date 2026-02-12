package com.bookstore.dto;

import lombok.Data;

@Data
public class BookQueryRequest {

    private Long categoryId;
    private String keyword;
    private String author;
    private String sortBy = "create_time";
    private String order = "desc";
    private long page = 1;
    private long size = 10;
}
