package com.bookstore.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadVO {

    private String fileUrl;
    private String originalName;
    private Long size;
}
