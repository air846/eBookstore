package com.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentReactionRequest {

    @NotNull(message = "reaction value can not be empty")
    private Integer value;
}
