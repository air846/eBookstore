package com.bookstore.service;

import com.bookstore.vo.UrgeStatVO;

import java.util.List;

public interface UrgeService {

    void create(Long userId, Long bookId);

    List<UrgeStatVO> listStats();
}
