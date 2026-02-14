package com.bookstore.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {
    private Long id;
    private Long bookId;
    private Long chapterId;
    private Integer paragraphIndex;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private Long parentId;
    private String content;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer userReaction;
    private Boolean hidden;
    private List<CommentVO> replies;
    private LocalDateTime createTime;
}
