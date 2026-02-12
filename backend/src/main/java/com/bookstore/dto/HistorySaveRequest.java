package com.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HistorySaveRequest {

    @NotNull(message = "书籍ID不能为空")
    private Long bookId;
    private String chapter;
    private String progress;
}
