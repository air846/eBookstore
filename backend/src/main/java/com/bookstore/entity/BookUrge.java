package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("book_urge")
public class BookUrge {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private Long userId;
    private LocalDateTime createTime;
}
