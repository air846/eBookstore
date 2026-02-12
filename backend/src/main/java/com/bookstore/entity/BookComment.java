package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("book_comment")
public class BookComment {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private Long chapterId;
    private Integer paragraphIndex;
    private Long userId;
    private Long parentId;
    private String content;
    private Integer status;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
