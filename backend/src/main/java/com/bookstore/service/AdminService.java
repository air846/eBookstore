package com.bookstore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookstore.entity.User;
import com.bookstore.vo.DashboardStatsVO;

import java.io.IOException;
import java.io.OutputStream;

public interface AdminService {

    DashboardStatsVO dashboard();

    IPage<User> pageUsers(long page, long size, String keyword);

    void updateUserStatus(Long id, Integer status);

    void resetUserPassword(Long id, String newPassword);

    void exportReport(OutputStream outputStream) throws IOException;
}
