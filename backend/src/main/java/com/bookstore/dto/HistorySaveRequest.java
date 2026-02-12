package com.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
// 阅读历史保存请求参数
public class HistorySaveRequest {

    @NotNull(message = "书籍ID不能为空")
    private Long bookId;
    private String chapter;
    private String progress;
}
