package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("book_chapter")
public class BookChapter {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private String title;
    private String content;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
