package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment_hide")
public class CommentHide {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long commentId;
    private Long userId;
    private LocalDateTime createTime;
}
