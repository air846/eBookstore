package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment_notice")
public class CommentNotice {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long commentId;
    private Long bookId;
    private Long chapterId;
    private Integer paragraphIndex;
    private Integer type;
    private Long fromUserId;
    private Integer readFlag;
    private LocalDateTime createTime;
}
