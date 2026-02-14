package com.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentCreateRequest {

    @NotNull(message = "paragraph index can not be empty")
    private Integer paragraphIndex;

    // For TXT virtual chapters, backend stores negative chapter key by this index.
    private Integer virtualChapterIndex;

    @NotBlank(message = "comment content can not be empty")
    private String content;

    private Long parentId;
}
