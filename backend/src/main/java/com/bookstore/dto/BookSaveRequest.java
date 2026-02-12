package com.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
// 书籍新增/编辑请求参数
public class BookSaveRequest {

    @NotBlank(message = "书名不能为空")
    private String title;

    @NotBlank(message = "作者不能为空")
    private String author;

    private String publisher;
    private String isbn;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private String coverUrl;
    private String description;

    private String fileUrl;

    @NotBlank(message = "文件类型不能为空")
    private String fileType;

    private Integer status = 0;
}
