package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("carousel")
public class Carousel {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String imageUrl;
    private String link;
    private Integer sortOrder;
    private Integer status;
}
