package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("book")
public class Book {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private Long categoryId;
    private String coverUrl;
    private String description;
    private String fileUrl;
    private String fileType;
    private Integer status;
    private Long visitCount;
    private LocalDateTime createTime;
}
