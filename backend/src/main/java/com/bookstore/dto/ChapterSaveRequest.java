package com.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChapterSaveRequest {

    @NotBlank(message = "chapter title can not be empty")
    private String title;

    @NotBlank(message = "chapter content can not be empty")
    private String content;

    private Integer sortOrder = 0;
}
