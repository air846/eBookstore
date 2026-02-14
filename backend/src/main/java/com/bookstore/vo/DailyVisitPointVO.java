package com.bookstore.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyVisitPointVO {

    private String date;
    private Long value;
}
