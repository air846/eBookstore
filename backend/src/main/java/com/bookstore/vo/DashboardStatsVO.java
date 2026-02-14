package com.bookstore.vo;

import com.bookstore.entity.Book;
import lombok.Data;

import java.util.List;

@Data
public class DashboardStatsVO {

    private Long totalUsers;
    private Long totalBooks;
    private Long todayVisits;
    private List<Book> hotBooks;
    private List<DailyVisitPointVO> dailyVisitTrend;
    private List<PreferenceStatVO> categoryBookStats;
}
