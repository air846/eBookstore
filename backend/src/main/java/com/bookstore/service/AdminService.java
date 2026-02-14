package com.bookstore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookstore.entity.User;
import com.bookstore.vo.DashboardStatsVO;

public interface AdminService {

    DashboardStatsVO dashboard();

    IPage<User> pageUsers(long page, long size, String keyword);

    void updateUserStatus(Long id, Integer status);

    void resetUserPassword(Long id, String newPassword);
}
